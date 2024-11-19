package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.LoanPaymentDto;
import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.dto.SaveLoanDto;
import al_hiro.com.Mkoba.Management.System.entity.Loan;
import al_hiro.com.Mkoba.Management.System.entity.LoanPayment;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.repository.LoanPaymentRepository;
import al_hiro.com.Mkoba.Management.System.repository.LoanRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Log
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    public Response<Loan> saveLoan(SaveLoanDto saveLoanDto) {
        if (saveLoanDto == null)
            return new Response<>("Data is required");
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
        } else loan = new Loan();

        if (saveLoanDto.getAmount() <= 0)
            return new Response<>("Loan amount must be greater than zero");
        if (saveLoanDto.getStartDate()==null)
            return new Response<>("Loan must have start date");
        if (saveLoanDto.getDueDate()==null)
            return new Response<>("Loan must have due date");

        loan.setAmount(saveLoanDto.getAmount());
        loan.setMember(existingMember.get());
        loan.setStartDate(saveLoanDto.getStartDate());
        loan.setDueDate(saveLoanDto.getDueDate());
        loan.setInterestRate(saveLoanDto.getInterestRate());
        try {
            loanRepository.save(loan);
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

    public Response<Loan> getMemberLoan(Long id) {
        if (id == null)
            return new Response<>("Loan identity required");
        Optional<Loan> optionalLoan = loanRepository.findById(id);
        if (optionalLoan.isEmpty())
            return new Response<>("Loan not found");
        return new Response<>(optionalLoan.get());
    }

    public Response<Loan> deleteMemberLoan(Long id) {
        if (id == null)
            return new Response<>("Loan identity required");
        Optional<Loan> optionalLoan = loanRepository.findById(id);
        if (optionalLoan.isEmpty())
            return new Response<>("Loan not found");
        Loan loan = optionalLoan.get();
        try {
            loanRepository.delete(loan);
            return new Response<>(loanRepository.save(optionalLoan.get()));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            return Response.error(msg);
        }
    }

    public ResponsePage<Loan> getLoanByMember(Long memberId, PageableParam pageableParam) {
        return new ResponsePage<>(loanRepository.getLoanByMember(memberId, pageableParam.getPageable(true), pageableParam.key()));
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
//            loanPayment.update();
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

        try {
            loanPaymentRepository.save(loanPayment);

            // Check if the payment is after the due date and apply a penalty if needed
            if (!existingLoan.getIsPaid() && LocalDate.now().isAfter(existingLoan.getDueDate()) && !existingLoan.getIsPenaltyApplied()) {
                double penalty = existingLoan.calculatePenalty(); // 10% penalty
                existingLoan.setIsPenaltyApplied(true);
                existingLoan.setAmount(existingLoan.getAmount() + penalty); // Apply penalty to loan amount
            }

            // Update the loan balance and payment status
            double remainingAmount = existingLoan.getAmount() - loanPaymentDto.getAmount();
            if (remainingAmount <= 0) {
                existingLoan.setAmount(0.0);
                existingLoan.setIsPaid(true);
            } else {
                existingLoan.setAmount(remainingAmount);
            }

            loanRepository.save(existingLoan);

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

    public ResponsePage<LoanPayment> getLoanPaymentsByMember(Long memberId, PageableParam pageableParam) {
        return new ResponsePage<>(loanPaymentRepository.getLoanPaymentsByMember(memberId, pageableParam.getPageable(true), pageableParam.key()));
    }
}
