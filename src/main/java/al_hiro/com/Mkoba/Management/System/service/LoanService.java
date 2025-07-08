package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.LoanPaymentDto;
import al_hiro.com.Mkoba.Management.System.dto.SaveLoanDto;
import al_hiro.com.Mkoba.Management.System.entity.Loan;
import al_hiro.com.Mkoba.Management.System.entity.LoanPayment;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.repository.ContributionRepository;
import al_hiro.com.Mkoba.Management.System.repository.LoanPaymentRepository;
import al_hiro.com.Mkoba.Management.System.repository.LoanRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log
public class LoanService {
    @Autowired
    private ContributionRepository contributionRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ContributionService contributionService;

    public Response<Loan> saveLoan(SaveLoanDto saveLoanDto) {
        if (saveLoanDto == null)
            return new Response<>("Data is required");

        // Validate member
        Optional<Member> existingMember = memberRepository.findById(saveLoanDto.getMemberId());
        if (existingMember.isEmpty())
            return new Response<>("Member does not exist");

        Loan loan;
        if (saveLoanDto.getId() != null) {
            Optional<Loan> optionalLoan = loanRepository.findById(saveLoanDto.getId());
            if (optionalLoan.isEmpty())
                return new Response<>("Loan not found");
            loan = optionalLoan.get();
            loan.update();
        } else {
            loan = new Loan();
        }

        // Validate input data
        if (saveLoanDto.getAmount() <= 0)
            return new Response<>("Loan amount must be greater than zero");
        if (saveLoanDto.getStartDate() == null)
            return new Response<>("Loan must have start date");
        if (saveLoanDto.getDueDate() == null)
            return new Response<>("Loan must have due date");
        if (saveLoanDto.getStartDate().isAfter(saveLoanDto.getDueDate()))
            return new Response<>("Start date must be before due date");

        // Calculate interest amount for the loan
        double interestAmount = saveLoanDto.getAmount() * saveLoanDto.getInterestRate();

        // Set loan details
        loan.setMember(existingMember.get());
        loan.setStartDate(saveLoanDto.getStartDate());
        loan.setDueDate(saveLoanDto.getDueDate());
        loan.setInterestRate(saveLoanDto.getInterestRate());
        loan.setAmount(saveLoanDto.getAmount());
        loan.setPayableAmount(saveLoanDto.getAmount()+interestAmount);
        loan.setUnpaidAmount(saveLoanDto.getAmount()+interestAmount);
        loan.setInterestAmount(interestAmount);

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
                memberShares = BigDecimal.ZERO;
            }

            // Calculate member's share percentage
            BigDecimal memberSharePercentage = memberShares.divide(groupSavings, 4, RoundingMode.HALF_UP);

            // Calculate member's interest
            BigDecimal memberInterest = BigDecimal.valueOf(interestAmount).multiply(memberSharePercentage);

            // Update member's shares
            BigDecimal updatedShares = memberShares.add(memberInterest);
            member.setMemberShares(updatedShares);
            memberRepository.save(member); // Save updated member shares
        }

        try {
            loanRepository.save(loan); // Save loan after interest calculation
            return new Response<>(loan);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            if (msg.contains("amount"))
                return new Response<>("Duplicate loan amount");
            if (msg.contains("duration"))
                return new Response<>("Duplicate loan duration");
            if (msg.contains("member"))
                return new Response<>("Duplicate member");
            return new Response<>("Could not save loan");
        }
    }

    public ResponsePage<Loan> getMembersLoans(PageableParam pageableParam) {
        return new ResponsePage<>(loanRepository.getMembersLoans(pageableParam.getPageable(true), pageableParam.key()));
    }

//    public Response<Loan> getMemberLoan(Long id) {
//        if (id == null)
//            return new Response<>("Loan identity required");
//        Optional<Loan> optionalLoan = loanRepository.findById(id);
//        if (optionalLoan.isEmpty())
//            return new Response<>("Loan not found");
//        return new Response<>(optionalLoan.get());
//    }

    public Response<Loan> deleteMemberLoan(Long id) {
        if (id == null)
            return new Response<>("Loan identity required");
        Optional<Loan> optionalLoan = loanRepository.findById(id);
        if (optionalLoan.isEmpty())
            return new Response<>("Loan not found");
        Loan loan = optionalLoan.get();
        try {
            loan.delete();
            return new Response<>(loanRepository.save(optionalLoan.get()));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            return Response.error(msg);
        }
    }

    public ResponsePage<Loan> getLoanByMember(Long memberId, PageableParam pageableParam, Integer year) {
        return new ResponsePage<>(loanRepository.getLoanByMember(memberId, pageableParam.getPageable(true), pageableParam.key(), year));
    }

    public Response<LoanPayment> saveLoanPayment(LoanPaymentDto loanPaymentDto) {
        if (loanPaymentDto == null)
            return new Response<>("Data is required");

        Optional<Loan> existingLoanOpt = loanRepository.findById(loanPaymentDto.getLoanId());
        if (existingLoanOpt.isEmpty())
            return new Response<>("Loan does not exist");
        Loan existingLoan = existingLoanOpt.get();

        LoanPayment loanPayment;
        if (loanPaymentDto.getId() != null) {
            Optional<LoanPayment> optionalLoanPayment = loanPaymentRepository.findById(loanPaymentDto.getId());
            if (optionalLoanPayment.isEmpty())
                return new Response<>("Loan payment not found");
            loanPayment = optionalLoanPayment.get();
            loanPayment.update();
        } else {
            loanPayment = new LoanPayment();
        }

        if (loanPaymentDto.getPayDate() == null)
            return new Response<>("Payment date is required");
        if (loanPaymentDto.getAmount() <= 0)
            return new Response<>("Payment amount must be greater than zero");
        if (loanPaymentDto.getLoanId() == null)
            return new Response<>("Respective Loan identity required");


        loanPayment.setPayDate(loanPaymentDto.getPayDate());
        loanPayment.setAmount(loanPaymentDto.getAmount());
        loanPayment.setDescription(loanPaymentDto.getDescription());
        loanPayment.setLoan(existingLoan);

        // Update the loan balance and payment status
        double remainingAmount = existingLoan.getUnpaidAmount() - loanPaymentDto.getAmount();
        if (remainingAmount <= 0) {
//            existingLoan.setAmount(0.0);
            existingLoan.setIsPaid(true);
            existingLoan.setUnpaidAmount(0.0);
        } else {
            existingLoan.setUnpaidAmount(remainingAmount);
        }
        try {
            loanPaymentRepository.save(loanPayment);
            return new Response<>(loanPayment);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            if (msg.contains("amount"))
                return new Response<>("Invalid payment amount");
            if (msg.contains("loan"))
                return new Response<>("Invalid loan provided");
            if (msg.contains("payDate"))
                return new Response<>("Payment date is invalid");
            return new Response<>("Could not save loan payment");
        }
    }

    public ResponsePage<LoanPayment> getLoanPaymentsByMember(Long memberId, PageableParam pageableParam, Integer year) {
        return new ResponsePage<>(loanPaymentRepository.getLoanPaymentsByMember(memberId, pageableParam.getPageable(true), pageableParam.key(), year));
    }

    public Double getGroupLoansProfit() {
        Double totalProfits = loanRepository.findTotalLoansProfit();
        return totalProfits != null ? totalProfits : 0.0;
    }

    public Double getLoansProfitByMonth(Month month, int year) {
        log.info("Getting total loan profits for a month");
        if (month == null) {
            month = LocalDateTime.now().getMonth();
        }
        if (year == 0) {
            year = LocalDateTime.now().getYear();
        }
        int monthValue = month.getValue(); // Convert to 1-12
        Double profitByMonth = loanRepository.findLoansProfitByMonth(monthValue,year);
        return profitByMonth != null ? profitByMonth : 0.0;
    }

    public Double getLoanTotalPenalties(Month month, int year) {
        Double loanTotalPenalties = loanRepository.findLoanPenalties(month, year);
        return loanTotalPenalties != null ? loanTotalPenalties : 0.0;
    }

    public Double getSumOfLoansForMonth(Month month) {
        Double totalMonthlyLoans = loanRepository.getSumOfLoansForMonth(month);
        return totalMonthlyLoans != null ? totalMonthlyLoans : 0.0;
    }

    public Double getGroupTotalPenalties(Month month, int year) {
        Double penaltiesFromLoan = getLoanTotalPenalties(month, year);
        Double penaltiesFromMonthlyContributions = contributionService.getContributionTotalPenalties(month, year);
        return penaltiesFromLoan + penaltiesFromMonthlyContributions;
    }

    public Double getCurrentMonthLoanPenalties(Month month) {
        LocalDate localDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        Month currentMonth = localDate.getMonth();
        if (month.equals(currentMonth.name())) {
            Double currentMonthLoanPenalties = loanRepository.findCurrentMonthLoanPenalties(month);
            return currentMonthLoanPenalties;
        } else {
            return 0.0;
        }
    }

//    public ResponsePage<Loan> getPenaltiesByMonth(Month month, PageableParam pageableParam) {
//        log.info("Getting Penalties by Month");
//        return new ResponsePage<>(loanRepository.getPenaltiesByMonth(month, pageableParam.getPageable(true), pageableParam.key()));
//    }

    public Double getTotalPenaltiesByMonth(Month month, int year) {
        log.info("Getting total penalties for a month");
        if (month == null) {
            month = LocalDateTime.now().getMonth();
        }
        int monthValue = month.getValue(); // Convert to 1-12
        Double totalMonthlyPenalties = loanRepository.findTotalPenaltiesByMonth(monthValue, year);
        return totalMonthlyPenalties != null ? totalMonthlyPenalties : 0.0;
    }

    public Double getGroupStandingDebt(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        Double totalDebt = loanRepository.findTotalDebt(year);
        return totalDebt != null ? totalDebt : 0.0;
    }

    public Double getTotalLoansByMember(Long memberId, Integer year) {
        log.info("Getting total loan for a member");
        Double totalLoansByMember = loanRepository.getTotalLoansByMember(memberId, year);
        return totalLoansByMember != null ? totalLoansByMember : 0.0;
    }

    public Double getTotalPenaltiesByMember(Long memberId, Integer year) {
        log.info("Getting member's total penalties");
        Double memberPenalties = loanRepository.findTotalPenaltiesByMember(memberId, year);
        return memberPenalties != null ? memberPenalties : 0.0;
    }

    public ResponsePage<Loan> getLatePaidLoansByMember(Long memberId, PageableParam pageableParam, Integer year) {
        return new ResponsePage<>(loanRepository.findLatePaidLoansByMember(memberId, pageableParam.getPageable(true), pageableParam.key(), year));
    }

    public ResponsePage<LoanPayment> getLoanPayments(PageableParam pageableParam, Integer year) {
        log.info("Getting all loan payments");
        return new ResponsePage<>(loanPaymentRepository.getLoanPayments(pageableParam.getPageable(true), pageableParam.key(), year));
    }

    @Scheduled(cron = "0 0 0 * * *") // Saa 6 usiku (00:00)
//    @Scheduled(cron = "0 * * * * *") // Run kila baada ya dakika 1
    @Transactional
    public void applyPenaltiesForOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findAllByIsPaidFalseAndIsPenaltyAppliedFalseAndDueDateBefore(LocalDate.now());

        if (overdueLoans.isEmpty()) return;

        List<Member> members = memberRepository.findAll();
        BigDecimal groupSavings = members.stream()
                .map(Member::getMemberShares)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (groupSavings.compareTo(BigDecimal.ZERO) == 0) {
            System.err.println("Total member shares cannot be zero.");
            return;
        }

        for (Loan loan : overdueLoans) {
            double penalty = loan.calculatePenalty(); // e.g. 10% of unpaid or payable amount
            loan.setPenaltyAmount(penalty);
            loan.setIsPenaltyApplied(true);
            loan.setPayableAmount(loan.getPayableAmount() + penalty);
            loan.setUnpaidAmount(loan.getUnpaidAmount() + penalty);

            for (Member member : members) {
                BigDecimal memberShares = member.getMemberShares();
                if (memberShares == null || memberShares.compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal memberSharePercentage = memberShares.divide(groupSavings, 4, RoundingMode.HALF_UP);
                BigDecimal penaltyDistribution = BigDecimal.valueOf(penalty).multiply(memberSharePercentage);

                BigDecimal updatedShares = memberShares.add(penaltyDistribution);
                member.setMemberShares(updatedShares);
            }

            loanRepository.save(loan); // Save updated loan
        }

        memberRepository.saveAll(members); // Save all updated members
        System.out.println("Penalties applied to overdue loans at midnight.");
    }

    public Response<LoanPayment> deleteLoanPayment(Long id) {
        if (id == null)
            return new Response<>("Loan payment identity required");
        Optional<LoanPayment> optionalLoanPayment = loanPaymentRepository.findById(id);
        if (optionalLoanPayment.isEmpty())
            return new Response<>("Loan not found");
        LoanPayment loanPayment = optionalLoanPayment.get();
        try {
            loanPayment.delete();
            return new Response<>(loanPaymentRepository.save(optionalLoanPayment.get()));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            return Response.error(msg);
        }
    }
}
