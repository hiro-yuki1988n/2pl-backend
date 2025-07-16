package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.CommonFundDto;
import al_hiro.com.Mkoba.Management.System.entity.CommonFund;
import al_hiro.com.Mkoba.Management.System.service.CommonFundService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
@RequiredArgsConstructor
public class CommonFundController {

    private final CommonFundService commonFundService;

    @GraphQLMutation(name = "saveCommonFund", description = "Saving Common fund")
    public Response<CommonFund> saveCommonFund(@GraphQLArgument(name = "commonFundDto")CommonFundDto commonFundDto) {
        return commonFundService.saveCommonFund(commonFundDto);
    }

    @GraphQLQuery(name = "getCommonFunds", description = "Get all common funds")
    public ResponsePage<CommonFund> getCommonFunds(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return commonFundService.getCommonFunds(pageableParam!=null?pageableParam:new PageableParam());
    }

    @GraphQLQuery(name = "getCommonFund", description = "Get common fund by id")
    public Response<CommonFund> getCommonFund(@GraphQLArgument(name = "id") Long id) {
        return commonFundService.getCommonFund(id);
    }

    @GraphQLMutation(name = "deleteCommonFund", description = "Delete common fund")
    public Response<String> deleteCommonFund(@GraphQLArgument(name = "id") Long id){
        return commonFundService.deleteCommonFund(id);
    }
}
