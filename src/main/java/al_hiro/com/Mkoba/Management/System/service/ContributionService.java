package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.ContributionDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.repository.ContributionRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;

    // Check if the payment was on time

    private Boolean isOnTime(LocalDateTime datePaid, Month month) {
        int year = Year.now().getValue();
        int monthNumber = month.getValue();
        int lastDayOfMonth = java.time.YearMonth.of(year, monthNumber).lengthOfMonth(); // Get last day of the month

        // Calculate the last allowed time (last day of the month at 23:59:59)
        LocalDateTime lastAllowedTime = LocalDateTime.of(year, monthNumber, lastDayOfMonth, 23, 59, 59);

        return !datePaid.isAfter(lastAllowedTime);
    }

    // Calculate the penalty if payment is late
//    private BigDecimal calculatePenalty(Boolean isOnTime, BigDecimal amount) {
//        if (!isOnTime) {
//            // Example: 10% penalty if late
//            return amount.multiply(BigDecimal.valueOf(0.10));
//        }
//        return BigDecimal.valueOf(0.0);
//    }

    private BigDecimal calculatePenalty(Boolean isOnTime, BigDecimal amount) {
        BigDecimal base = BigDecimal.valueOf(100000);
        if (!isOnTime) {
            // 10% ya 100000 = 10000
            return base.multiply(BigDecimal.valueOf(0.10));
        }
        return BigDecimal.ZERO;
    }

    public Response<Contribution> saveContribution(ContributionDto contributionDto) {
        log.info("Saving contribution");
        if (contributionDto == null)
            return Response.warning(null, "Contribution is required");

        if (contributionDto.getAmount() == null)
            return Response.warning(null, "Amount paid is required");
        if (contributionDto.getMonth() == null)
            return Response.warning(null, "Month paid for is required");
        if (contributionDto.getMemberId() == null)
            return Response.warning(null, "Member who paid is required");
//        if (contributionDto.getPaymentDate() == null)
//            return Response.warning(null, "Payment date is required");

        Contribution contribution;

        // Check if contribution for the member and month already exists
        Optional<Contribution> existingContribution =
                contributionRepository.findByMemberAndMonth(contributionDto.getMemberId(), contributionDto.getMonth());

        if (existingContribution.isPresent()) {
            contribution = existingContribution.get();
            log.info("Updating existing contribution for member: " + contributionDto.getMemberId() + " and month: " + contributionDto.getMonth());
            BigDecimal newAmount = contribution.getAmount().add(contributionDto.getAmount());
            contribution.setAmount(newAmount);
            contribution.setPaymentDate(LocalDateTime.now());
        } else {
            log.info("Creating new contribution for member: " + contributionDto.getMemberId() + " and month: " + contributionDto.getMonth());
            contribution = new Contribution();
            contribution.setAmount(contributionDto.getAmount());
            contribution.setMonth(contributionDto.getMonth());
            contribution.setYear(Year.now().getValue());
            contribution.setMember(Utils.entity(Member.class, contributionDto.getMemberId()));
            contribution.setPaymentDate(LocalDateTime.now());
            contribution.setContributionCategory(contributionDto.getContributionCategory());
        }

        Optional<Member> optMember = memberRepository.findById(contributionDto.getMemberId());
        if (optMember.isPresent()) {
            Member member = optMember.get();

            BigDecimal existingMemberShares = member.getMemberShares();
            if (existingMemberShares == null) {
                existingMemberShares = BigDecimal.ZERO;
            }

            BigDecimal updatedMemberShares = existingMemberShares.add(contributionDto.getAmount());
            member.setMemberShares(updatedMemberShares);
            memberRepository.save(member);
        }

        // Check if payment was on time
        if (!isOnTime(contribution.getPaymentDate(), contribution.getMonth())) {
            contribution.setOnTime(false);
            contribution.setPenaltyApplied(true);
            BigDecimal memberPenaltyAmount = calculatePenalty(false, contribution.getAmount());
//            BigDecimal memberPenaltyAmount = BigDecimal.valueOf(10000);
            contribution.setPenaltyAmount(memberPenaltyAmount);

            List<Member> members = memberRepository.findAll(); // Fetch all group members

            // Calculate total group savings using BigDecimal
            BigDecimal groupSavings = members.stream()
                    .map(Member::getMemberShares)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (groupSavings.compareTo(BigDecimal.ZERO) == 0) {
                return new Response<>("Total shares cannot be zero");
            }

            for (Member member : members) {
                BigDecimal memberShares = member.getMemberShares();
                if (memberShares == null || memberShares.compareTo(BigDecimal.ZERO) < 0) {
//                    return new Response<>("Member shares must be non-null and non-negative");
                    memberShares = BigDecimal.ZERO;
                }

                // Calculate member's share percentage
                BigDecimal memberSharePercentage = memberShares.divide(groupSavings, 4, RoundingMode.HALF_UP);

                // Calculate member's interest
                BigDecimal memberInterest = memberPenaltyAmount.multiply(memberSharePercentage);

                // Update member's shares
                BigDecimal updatedShares = memberShares.add(memberInterest);
                member.setMemberShares(updatedShares);
                memberRepository.save(member); // Save updated member shares
            }
        }
        if (isOnTime(contribution.getPaymentDate(), contribution.getMonth())) {
            contribution.setOnTime(true);
            contribution.setPenaltyApplied(false);
            contribution.setPenaltyAmount(calculatePenalty(true, contribution.getAmount()));
        }

        // Save contribution to database
        try {
            contributionRepository.save(contribution);
            return new Response<>(contribution);
        } catch (Exception e) {
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if (message.contains("month"))
                return new Response<>("Invalid Month");
            if (message.contains("(memberId)"))
                return new Response<>("Invalid Member");
            if (message.contains("amount"))
                return new Response<>("Invalid Amount");
            return new Response<>("Could not save monthly Contribution for a member");
        }
    }

    public ResponsePage<Contribution> getContributions(PageableParam pageableParam) {
        log.info("Getting contributions");
        return new ResponsePage<>(contributionRepository.getContributions(pageableParam.getPageable(true), pageableParam.getIsActive(), pageableParam.key()));
    }

    public Response<Contribution> getContribution(Long id) {
        log.info("Getting one contribution");
        if (id == null)
            return Response.warning(null, "Contribution ID is required");
        Optional<Contribution> optionalContribution = contributionRepository.findById(id);
        return optionalContribution.map(Response::new).orElseGet(() -> Response.error("Contribution not found"));
    }

    public ResponsePage<Contribution> getContributionsByMember(PageableParam pageableParam, Long id, Integer year) {
        log.info("Getting contributions by member");
        if (id == null)
            return new ResponsePage<>("Member ID is required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
            return new ResponsePage<>("Member not found");
        return new ResponsePage<>(contributionRepository.getAllByMember(pageableParam.getPageable(true),
                pageableParam.getIsActive(), pageableParam.key(), id, year));
    }

    public ResponsePage<Contribution> getMonthlyContributions(PageableParam pageableParam, String month) {
        log.info("Getting all monthly contributions");
        if (month == null)
            return new ResponsePage<>("Month is required");
//        YearMonth yearMonth = processMonth(month);
//        fetchDataForMonth(yearMonth);
        return new ResponsePage<>(contributionRepository.getAllByMonth(pageableParam.getPageable(true), pageableParam.getIsActive(), month));
    }

    public Response<Contribution> deleteContribution(Long id) {
        log.info("Deleting contribution");
        if (id == null)
            return Response.warning(null, "Contribution ID is required");
        Optional<Contribution> optionalContribution = contributionRepository.findById(id);
        if (optionalContribution.isEmpty())
            return Response.warning(null, "Contribution not found");
        Contribution contribution = optionalContribution.get();
        contribution.delete();
        try {
            return new Response<>(contributionRepository.save(contribution));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Could not delete monthly Contribution for a member");
        }
    }

    public Double getTotalContributions() {
        log.info("Getting total contributions");
        Double totalContributions = contributionRepository.getTotalContributions();
        return totalContributions != null ? totalContributions : 0.0;
    }

    public Double getTotalMemberContributions(Long memberId, Integer year) {
        log.info("Getting total contributions for a member");
        if (memberId == null) {
            return null; // or throw an exception or return a default value
        }
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return null; // or throw an exception or return a default value
        }
        Double totalContributions = contributionRepository.getTotalContributionsByMember(memberId, year);
        return totalContributions != null ? totalContributions : 0.0;
    }

    public Double getContributionTotalPenalties(Month month, Integer year) {
        Double contributionTotalPenalties = contributionRepository.findContributionPenalties(month.getValue(), year);
        return contributionTotalPenalties != null ? contributionTotalPenalties : 0.0;
    }

    public Double getTotalContributionsByMonthAndYear(Month month, Integer year) {
        log.info("Getting total contributions for a month and year");
        if (month == null) {
            month = LocalDateTime.now().getMonth();
        }
        if (year == null) {
            year = Year.now().getValue();
        }
        Double totalContributionsByMonthAndYear = contributionRepository.getTotalContributionsByMonthAndYear(month, year);
        return totalContributionsByMonthAndYear != null ? totalContributionsByMonthAndYear : 0.0;
    }

    public ResponsePage<Contribution> getLateContributionsByMember(PageableParam pageableParam, Long id, Integer year) {
        log.info("Getting late contributions by member");
        if (id == null)
            return new ResponsePage<>("Member ID is required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
            return new ResponsePage<>("Member not found");
        return new ResponsePage<>(contributionRepository.findAllByMember(pageableParam.getPageable(true),
                pageableParam.getIsActive(), pageableParam.key(), id, year));
    }
}
