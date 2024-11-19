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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;

    // Check if the payment was on time
    private Boolean isOnTime(LocalDateTime datePaid, String month) {
        int year = Integer.parseInt(month.split("-")[0]);
        int monthNumber = Integer.parseInt(month.split("-")[1]);
        int lastDayOfMonth = java.time.YearMonth.of(year, monthNumber).lengthOfMonth(); // Get last day of the month

        // Calculate the last allowed time (last day of the month at 23:59:59)
        LocalDateTime lastAllowedTime = LocalDateTime.of(year, monthNumber, lastDayOfMonth, 23, 59, 59);

        return !datePaid.isAfter(lastAllowedTime);
    }

    // Calculate the penalty if payment is late
    private BigDecimal calculatePenalty(Boolean isOnTime, BigDecimal amount) {
        if (!isOnTime) {
            // Example: 10% penalty if late
            return amount.multiply(BigDecimal.valueOf(0.10));
        }
        return BigDecimal.valueOf(0.0);
    }

    public Response<Contribution> saveContribution(ContributionDto contributionDto) {
        log.info("Saving contribution");
        if (contributionDto == null)
            return Response.warning(null,"Contribution is required");

        Contribution contribution;
        if (contributionDto.getId() != null) {
            Optional<Contribution> optionalContribution = contributionRepository.findById(contributionDto.getId());
            if (optionalContribution.isEmpty())
                return Response.warning(null,"Contribution not found");
            contribution = optionalContribution.get();
            contribution.update();
        } else{
            if(contributionRepository.findByMemberAndMonth(contributionDto.getMemberId(), contributionDto.getMonth()).isPresent())
                return Response.warning(null,"Contribution for this member and month already exists");
            contribution = new Contribution();
        }

        if (contributionDto.getAmount() == null)
            return Response.warning(null,"Amount paid is required");
        if (contributionDto.getMonth() == null)
            return Response.warning(null,"Month paid for is required");
        if (contributionDto.getMemberId() == null)
            return Response.warning(null,"Member who paid is required");

        contribution.setAmount(contributionDto.getAmount());
        contribution.setMonth(contributionDto.getMonth());
        contribution.setMember(Utils.entity(Member.class, contributionDto.getMemberId()));
        contribution.setPaymentDate(LocalDateTime.now());

        // Check if payment was on time
        if(!isOnTime(contribution.getPaymentDate(), contribution.getMonth())){
            contribution.setOnTime(false);
            contribution.setPenaltyApplied(true);
            contribution.setPenaltyAmount(calculatePenalty( false, contribution.getAmount()));
        } else{
            contribution.setOnTime(true);
            contribution.setPenaltyApplied(false);
            contribution.setPenaltyAmount(calculatePenalty( true, contribution.getAmount()));
        }

        // Save contribution to database
        try{
            contributionRepository.save(contribution);
            return new Response<>(contribution);
        } catch (Exception e){
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if(message.contains("month"))
                return new Response<>("Invalid Month");
            if(message.contains("(memberId)"))
                return new Response<>("Invalid Member");
            if(message.contains("amount"))
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
            return Response.warning(null,"Contribution ID is required");
        Optional<Contribution> optionalContribution = contributionRepository.findById(id);
        return optionalContribution.map(Response::new).orElseGet(() -> Response.error("Contribution not found"));
    }

    public ResponsePage<Contribution> getContributionsByMember(PageableParam pageableParam, Long id) {
        log.info("Getting contributions by member");
        if (id == null)
            return new ResponsePage<>("Member ID is required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
            return new ResponsePage<>("Member not found");
        return new ResponsePage<>(contributionRepository.getAllByMember(pageableParam.getPageable(true),
                pageableParam.getIsActive(), pageableParam.key(), id));
    }

    public ResponsePage<Contribution> getMonthlyContributions(PageableParam pageableParam, String month) {
        log.info("Getting all monthly contributions");
        if (month == null)
            return new ResponsePage<>("Month is required");
//        YearMonth yearMonth = processMonth(month);
//        fetchDataForMonth(yearMonth);
        return new ResponsePage<>(contributionRepository.getAllByMonth(pageableParam.getPageable(true), pageableParam.getIsActive(), month));
    }

//    private void fetchDataForMonth(YearMonth yearMonth) {
//        if (yearMonth != null) {
//            // Your database query logic to fetch data based on the yearMonth
//            System.out.println("Fetching data for " + yearMonth);
//        } else {
//            System.out.println("Invalid month, cannot fetch data.");
//        }
//    }

//    private YearMonth processMonth(String month) {
//        try {
//            // Parse the month input (e.g., "2024-10") into a YearMonth object
//            return YearMonth.parse(month);
//        } catch (DateTimeParseException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public Response<Contribution> deleteContribution(Long id) {
        log.info("Deleting contribution");
        if (id == null)
            return Response.warning(null,"Contribution ID is required");
        Optional<Contribution> optionalContribution = contributionRepository.findById(id);
        if (optionalContribution.isEmpty())
            return Response.warning(null,"Contribution not found");
        Contribution contribution = optionalContribution.get();
        contribution.delete();
        try{
            return new Response<>(contributionRepository.save(contribution));
        } catch (Exception e){
            e.printStackTrace();
            return Response.error("Could not delete monthly Contribution for a member");
        }
    }
}
