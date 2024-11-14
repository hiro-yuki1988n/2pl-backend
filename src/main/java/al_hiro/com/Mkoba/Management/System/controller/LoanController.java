package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.dto.SaveLoanDto;
import al_hiro.com.Mkoba.Management.System.entity.Loan;
import al_hiro.com.Mkoba.Management.System.entity.Member;
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

    @GraphQLQuery(name = "getMemberLoan", description = "Getting member loan by id")
    public Response<Loan> getMemberLoan(@GraphQLArgument(name = "id") Long id) {
        return loanService.getMemberLoan(id);
    }

    @GraphQLMutation(name = "deleteMemberLoan", description = "Deleting a member loan by id")
    public Response<Loan> deleteMemberLoan(@GraphQLArgument(name = "id") Long id) {
        return loanService.deleteMemberLoan(id);
    }

}
