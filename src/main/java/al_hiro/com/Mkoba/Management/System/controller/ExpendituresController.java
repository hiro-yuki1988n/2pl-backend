package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.ExpendituresDto;
import al_hiro.com.Mkoba.Management.System.entity.Expenditures;
import al_hiro.com.Mkoba.Management.System.service.ExpendituresService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@GraphQLApi
@RequiredArgsConstructor
public class ExpendituresController {

    private final ExpendituresService expendituresService;

    @GraphQLMutation(name = "saveExpenditure", description = "Save Expenditure - SAVE_EXPENDITURE")
    public Response<Expenditures> saveExpenditure(@GraphQLArgument(name = "expendituresDto") ExpendituresDto expendituresDto) {
        return expendituresService.saveExpenditure(expendituresDto);
    }

    @GraphQLQuery(name = "getExpenditures", description = "Get Expenditures - GET_EXPENDITURES")
    public ResponsePage<Expenditures> getExpenditures(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                      @GraphQLArgument(name = "year") Integer year) {
        return expendituresService.getExpenditures(pageableParam!=null?pageableParam:new PageableParam(), year);
    }

    @GraphQLMutation(name = "approveExpenditure", description = "Approve Expenditure - APPROVE_EXPENDITURE")
    public Response<Expenditures> approveExpenditure(@GraphQLArgument(name = "expenditureId") Long expenditureId) {
        return expendituresService.approveExpenditure(expenditureId);
    }

    @GraphQLMutation(name = "deleteExpenditure", description = "Delete Expenditure - DELETE_EXPENDITURE")
    public Response<Expenditures> deleteExpenditure(@GraphQLArgument(name = "expenditureId") Long expenditureId) {
        expendituresService.deleteExpenditure(expenditureId);
        return new Response<>();
    }

    @GraphQLQuery(name = "getTotalExpenditures", description = "Get total group Expenditures")
    public Response<Double> getTotalExpenditures() {
        Double totalExpenses = expendituresService.calculateTotalExpenditures();
        return new Response<>(totalExpenses);
    }
}

