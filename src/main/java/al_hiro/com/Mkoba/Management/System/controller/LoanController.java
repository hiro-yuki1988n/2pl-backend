package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.LoanPaymentDto;
import al_hiro.com.Mkoba.Management.System.dto.SaveLoanDto;
import al_hiro.com.Mkoba.Management.System.entity.Loan;
import al_hiro.com.Mkoba.Management.System.entity.LoanPayment;
import al_hiro.com.Mkoba.Management.System.service.LoanService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@Service
@GraphQLApi
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GraphQLMutation(name = "saveLoan", description = "Saving Loan")
    public Response<Loan> saveLoan(@GraphQLArgument(name = "saveLoanDto") SaveLoanDto saveLoanDto) {
        return loanService.saveLoan(saveLoanDto);
    }

    @GraphQLQuery(name = "getMembersLoans", description = "Getting a page of all members' loans")
    public ResponsePage<Loan> getMembersLoans(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return loanService.getMembersLoans(pageableParam!=null?pageableParam:new PageableParam());
    }

//    @GraphQLQuery(name = "getMemberLoan", description = "Getting member loan by id")
//    public Response<Loan> getMemberLoan(@GraphQLArgument(name = "id") Long id) {
//        return loanService.getMemberLoan(id);
//    }

    @GraphQLMutation(name = "deleteMemberLoan", description = "Deleting a member loan by id")
    public Response<Loan> deleteMemberLoan(@GraphQLArgument(name = "id") Long id) {
        return loanService.deleteMemberLoan(id);
    }

    @GraphQLQuery(name = "getLoanByMember", description = "Getting a page of loans by a specific member")
    public ResponsePage<Loan> getLoanByMember(@GraphQLArgument(name = "memberId") Long memberId,
                                              @GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                              @GraphQLArgument(name = "year") Integer year) {
        return loanService.getLoanByMember(memberId, pageableParam!=null?pageableParam:new PageableParam(), year);
    }

    @GraphQLMutation(name = "saveLoanPayment", description="Saving Loan Payments for a Member")
    public Response<LoanPayment> saveLoanPayment(@GraphQLArgument(name = "loanPaymentDto")LoanPaymentDto loanPaymentDto) {
        return loanService.saveLoanPayment(loanPaymentDto);
    }

    @GraphQLQuery(name = "getLoanPayments", description = "Getting Loan Payments")
    public ResponsePage<LoanPayment> getLoanPayments(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                             @GraphQLArgument(name = "year") Integer year) {
        return loanService.getLoanPayments(pageableParam!=null?pageableParam:new PageableParam(), year);
    }

    @GraphQLQuery(name = "getLoanPaymentsByMember", description = "Getting Loan Payments for a specific Member")
    public ResponsePage<LoanPayment> getLoanPaymentsByMember(@GraphQLArgument(name = "memberId") Long memberId,
                                                             @GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                             @GraphQLArgument(name = "year") Integer year) {
        return loanService.getLoanPaymentsByMember(memberId, pageableParam!=null?pageableParam:new PageableParam(), year);
    }

    @GraphQLMutation(name = "deleteLoanPayment", description = "Deleting a member loan payment by id")
    public Response<LoanPayment> deleteLoanPayment(@GraphQLArgument(name = "id") Long id) {
        return loanService.deleteLoanPayment(id);
    }

    @GraphQLQuery(name = "getGroupLoansProfit", description = "Getting Group's profit from Loans")
    public Response<Double> getGroupLoansProfit() {
        Double totalProfits = loanService.getGroupLoansProfit();
        return new Response<>(totalProfits);
    }

    @GraphQLQuery(name = "getLoansProfitByMonth", description = "Getting Group's profit from Loans")
    public Response<Double> getLoansProfitByMonth(@GraphQLArgument(name = "month") Month month, @GraphQLArgument(name = "year") Integer year) {
        Double profitByMonth = loanService.getLoansProfitByMonth(month, year);
        return new Response<>(profitByMonth);
    }

    @GraphQLQuery(name = "getLoanTotalPenalties", description = "Getting Group's profit from loan penalties")
    public Response<Double> getLoanTotalPenalties(@GraphQLArgument(name = "month") Month month, @GraphQLArgument(name = "year") Integer year) {
        Double loanTotalPenalties = loanService.getLoanTotalPenalties(month, year);
        return new Response<>(loanTotalPenalties);
    }

    @GraphQLQuery(name = "getCurrentMonthLoanPenalties", description = "Getting Group's profit from loan penalties")
    public Response<Double> getCurrentMonthLoanPenalties(@GraphQLArgument(name = "month") Month month) {
        Double currentMonthLoanPenalties = loanService.getCurrentMonthLoanPenalties(month);
        return new Response<>(currentMonthLoanPenalties);
    }

    //TO DO: NOT COMPLETED
    @GraphQLQuery(name = "getTotalPenaltiesByMonth", description = "Getting total amount of penalties for a month")
    public Response<Double> getTotalPenaltiesByMonth(@GraphQLArgument(name = "month") Month month, @GraphQLArgument(name = "year") Integer year) {
        Double totalMonthlyPenalties = loanService.getTotalPenaltiesByMonth(month, year);
        return new Response<>(totalMonthlyPenalties);
    }

    @GraphQLQuery(name = "getSumOfLoansForMonth", description = "Getting amount of Loans for month")
    public Response<Double> getSumOfLoansForMonth(@GraphQLArgument(name = "month") Month month) {
        Double totalMonthlyLoans = loanService.getSumOfLoansForMonth(month);
        return new Response<>(totalMonthlyLoans);
    }

    @GraphQLQuery(name = "getGroupTotalPenalties", description = "Getting Group's earnings from all penalties")
    public Response<Double> getGroupTotalPenalties(@GraphQLArgument(name = "month") Month month, @GraphQLArgument(name = "year") Integer year) {
        Double totalPenalties = loanService.getGroupTotalPenalties(month, year);
        return new Response<>(totalPenalties);
    }

    @GraphQLQuery(name = "getGroupStandingDebt", description = "Getting Group's total standing debts")
    public Response<Double> getGroupStandingDebt(@GraphQLArgument(name = "year") Integer year) {
        Double totalStandingDebt = loanService.getGroupStandingDebt(year);
        return new Response<>(totalStandingDebt);
    }

    @GraphQLQuery(name = "getTotalLoansByMember", description = "Getting Total Loan by member")
    public Response<Double> getTotalLoansByMember(@GraphQLArgument(name = "memberId") Long memberId, @GraphQLArgument(name = "year") Integer year) {
        Double totalLoansByMember = loanService.getTotalLoansByMember(memberId, year);
        return new Response<>(totalLoansByMember);
    }

    @GraphQLQuery(name = "getTotalPenaltiesByMember", description = "Getting Group's profit from all penalties")
    public Response<Double> getTotalPenaltiesByMember(@GraphQLArgument(name = "memberId") Long memberId, @GraphQLArgument(name = "year") Integer year) {
        Double memberPenalties = loanService.getTotalPenaltiesByMember(memberId, year);
        return new Response<>(memberPenalties);
    }

    @GraphQLQuery(name = "getLatePaidLoansByMember", description = "Getting a page of loans by a specific member")
    public ResponsePage<Loan> getLatePaidLoansByMember(@GraphQLArgument(name = "memberId") Long memberId,
                                              @GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                              @GraphQLArgument(name = "year") Integer year) {
        return loanService.getLatePaidLoansByMember(memberId, pageableParam!=null?pageableParam:new PageableParam(), year);
    }
}
