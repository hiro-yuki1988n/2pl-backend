package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.ContributionDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.enums.ContributionCategory;
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

    public Response<Contribution> saveContribution(ContributionDto dto) {
        log.info("Saving contribution");

        //Validate input
        String validationError = validateContributionDto(dto);
        if (validationError != null) {
            return Response.warning(null, validationError);
        }

        Contribution contribution;

        //Fetch member
        Member member = memberRepository.findById(dto.getMemberId()).orElse(null);
        if (member == null) {
            return Response.warning(null, "Member not found");
        }

        //Handle contributions by category
        switch (dto.getContributionCategory()) {
            case SHARE:
                contribution = handleShareContribution(dto, member);
                break;
            case ENTRY_FEE:
                contribution = handleEntryFeeContribution(dto, member);
                break;
            default:
                return Response.warning(null, "Unsupported contribution category");
        }

        //Handle penalty
        applyPenaltyIfRequired(contribution, member);

        //Save final contribution
        try {
            contributionRepository.save(contribution);
            return new Response<>(contribution);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error saving contribution" + e);
            return handleSaveException(e);
        }
    }

    private String validateContributionDto(ContributionDto dto) {
        if (dto == null) return "Contribution is required";
        if (dto.getAmount() == null) return "Amount paid is required";
        if (dto.getMonth() == null) return "Month paid for is required";
        if (dto.getMemberId() == null) return "Member who paid is required";
        if (dto.getContributionCategory() == null) return "Contribution category is required";
        return null;
    }

    private Contribution handleShareContribution(ContributionDto dto, Member member) {
        Contribution contribution;

        if (dto.getId() != null) {
            //Editing existing contribution
            Optional<Contribution> optionalContribution = contributionRepository.findById(dto.getId());

            if (optionalContribution.isPresent()) {
                contribution = optionalContribution.get();

                // Check if it belongs to the same member
                if (!contribution.getMember().getId().equals(dto.getMemberId())) {
                    throw new IllegalArgumentException("Contribution does not belong to this member");
                }

                log.info("Editing existing SHARE contribution with id {} " + dto.getId());

                // Update details
                contribution.setAmount(dto.getAmount());
                contribution.setMonth(dto.getMonth());
                contribution.setYear(Year.now().getValue());
                contribution.setPaymentDate(LocalDateTime.now());
                contribution.setContributionCategory(ContributionCategory.SHARE);
                contribution.update();

                // Update member shares (adjust by difference instead of re-adding full amount)
                BigDecimal oldAmount = contribution.getAmount() != null ? contribution.getAmount() : BigDecimal.ZERO;
                BigDecimal difference = dto.getAmount().subtract(oldAmount);

                BigDecimal existingShares = member.getMemberShares() != null ? member.getMemberShares() : BigDecimal.ZERO;
                member.setMemberShares(existingShares.add(difference));
                memberRepository.save(member);

                return contribution;
            }
        }

        //Creating new contribution
        log.info("Creating new SHARE contribution for member {} " + dto.getMemberId());
        contribution = new Contribution();
        contribution.setAmount(dto.getAmount());
        contribution.setMonth(dto.getMonth());
        contribution.setYear(Year.now().getValue());
        contribution.setMember(member);
        contribution.setContributionCategory(ContributionCategory.SHARE);
        contribution.setPaymentDate(LocalDateTime.now());

        // Update member shares
        BigDecimal existingShares = member.getMemberShares() != null ? member.getMemberShares() : BigDecimal.ZERO;
        member.setMemberShares(existingShares.add(dto.getAmount()));
        memberRepository.save(member);

        return contribution;
    }

    private Contribution handleEntryFeeContribution(ContributionDto dto, Member member) {
        log.info("Saving ENTRY_FEE contribution");
        Contribution contribution = new Contribution();
        contribution.setAmount(dto.getAmount());
        contribution.setMonth(dto.getMonth());
        contribution.setYear(Year.now().getValue());
        contribution.setMember(member);
        contribution.setPaymentDate(LocalDateTime.now());
        contribution.setContributionCategory(ContributionCategory.ENTRY_FEE);
        return contribution;
    }

    private void applyPenaltyIfRequired(Contribution contribution, Member member) {
        if (!isOnTime(contribution.getPaymentDate(), contribution.getMonth())) {
            contribution.setOnTime(false);
            contribution.setPenaltyApplied(true);

            BigDecimal penalty = calculatePenalty(false, contribution.getAmount());
            contribution.setPenaltyAmount(penalty);

            distributePenaltyAmongMembers(penalty);
        } else {
            contribution.setOnTime(true);
            contribution.setPenaltyApplied(false);
            contribution.setPenaltyAmount(calculatePenalty(true, contribution.getAmount()));
        }
    }

    private void distributePenaltyAmongMembers(BigDecimal penaltyAmount) {
        List<Member> members = memberRepository.findAllAndActive();

        BigDecimal totalShares = members.stream()
                .map(Member::getMemberShares)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalShares.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Total shares cannot be zero");
        }

        for (Member m : members) {
            BigDecimal mShares = m.getMemberShares() != null ? m.getMemberShares() : BigDecimal.ZERO;
            BigDecimal percentage = mShares.divide(totalShares, 4, RoundingMode.HALF_UP);
            BigDecimal interest = penaltyAmount.multiply(percentage);
            m.setMemberShares(mShares.add(interest));
            memberRepository.save(m);
        }
    }

    private Response<Contribution> handleSaveException(Exception e) {
        String message = Utils.getExceptionMessage(e);
        if (message.contains("month"))
            return new Response<>("Invalid Month");
        if (message.contains("(memberId)"))
            return new Response<>("Invalid Member");
        if (message.contains("amount"))
            return new Response<>("Invalid Amount");
        return new Response<>("Could not save monthly Contribution for a member");
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
        Double contributionTotalPenalties = contributionRepository.findContributionPenalties(month, year);
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

    public Double getTotalEntryFees(ContributionCategory category) {
        Double totalTotalEntryFees = contributionRepository.findTotalEntryFees(category);
        return totalTotalEntryFees != null ? totalTotalEntryFees : 0.0;
    }

    public Response<Contribution> insertEntryFeeContributions(BigDecimal amount, Integer year, String month) {
        log.info("Inserting entry fee contributions");
        try {
            if (amount == null || year == null || month == null)
                return Response.warning(null, "Amount, year, and month are required");
            contributionRepository.insertEntryFeeContributions(amount, year, month);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Could not insert entry fee contributions");
        }
        return Response.success(null);
    }
}
