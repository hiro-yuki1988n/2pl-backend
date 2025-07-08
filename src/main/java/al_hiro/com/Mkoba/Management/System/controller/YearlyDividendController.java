package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import al_hiro.com.Mkoba.Management.System.service.YearlyDividendService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@GraphQLApi
public class YearlyDividendController {

    private final YearlyDividendService yearlyDividendService;

    @GraphQLMutation(name = "approveDividend", description = "Approving yearly dividend for member")
    public Response<YearlyDividend> approveDividend(@GraphQLArgument(name = "yearlyDividendId") Long yearlyDividendId) {
        return yearlyDividendService.approveDividend(yearlyDividendId);
    }

    @GraphQLQuery(name = "getYearlyDividends", description = "Getting list of members' yearly dividends")
    public ResponsePage<YearlyDividend> getYearlyDividends(@GraphQLArgument(name = "pageableParam")PageableParam pageableParam){
        return yearlyDividendService.getYearlyDividends(pageableParam!=null?pageableParam:new PageableParam());
    }
}
