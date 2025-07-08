package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.ContributionDto;
import al_hiro.com.Mkoba.Management.System.dto.SocialFundDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.entity.SocialFund;
import al_hiro.com.Mkoba.Management.System.service.SocialFundService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Map;

@Service
@GraphQLApi
public class SocialFundController {

    @Autowired
    private SocialFundService socialFundService;

    @GraphQLMutation(name = "saveSocialFund", description = "Saving Social Fund - SAVE_SOCIAL_FUND")
    public Response<SocialFund> saveSocialFund(@GraphQLArgument(name = "socialFundDto") SocialFundDto socialFundDto) {
        return socialFundService.saveSocialFund(socialFundDto);
    }

    @GraphQLQuery(name = "getAllSocialFunds", description = "Getting a list of Social funds")
    public ResponsePage<SocialFund> getAllSocialFunds(@GraphQLArgument(name = "pageableParam")PageableParam pageableParam){
        return socialFundService.getAllSocialFunds(pageableParam!=null?pageableParam:new PageableParam());
    }

    @GraphQLQuery(name = "getSocialFundsByMember", description = "Getting a list of Social funds for a member")
    public ResponsePage<SocialFund> getSocialFundsByMember(@GraphQLArgument(name = "pageableParam")PageableParam pageableParam,
                                                           @GraphQLArgument(name = "memberId") Long memberId,
                                                           @GraphQLArgument(name = "year") Integer year){
        return socialFundService.getSocialFundsByMember(pageableParam!=null?pageableParam:new PageableParam(), memberId, year);
    }

    @GraphQLQuery(name = "getSocialFundsByMonth", description = "Getting a list of Social funds by month")
    public ResponsePage<SocialFund> getSocialFundsByMonth(@GraphQLArgument(name = "pageableParam")PageableParam pageableParam, @GraphQLArgument(name = "month") Month month){
        return socialFundService.getSocialFundsByMonth(month, pageableParam!=null?pageableParam:new PageableParam());
    }

//    @GraphQLQuery(name = "getTotalSocialFunds", description = "Getting Group's total social fund")
//    public Response<Double> getTotalSocialFunds() {
//        Double totalSocialFunds = socialFundService.getTotalSocialFunds();
//        return new Response<>(totalSocialFunds);
//    }

    @GraphQLQuery(name = "getTotalSocialFunds" , description = "Getting Group's total social fund")
    public Response<Double> getTotalSocialFunds() {
        Double totalSocialFunds = socialFundService.getTotalSocialFunds();
        return new Response<>(totalSocialFunds);
    }


    @GraphQLQuery(name = "getTotalSocialFundsByMonth", description = "Getting Group's total social funds by month")
    public Response<Double> getTotalSocialFundsByMonth(@GraphQLArgument(name = "month") String month) {
        Double totalSocialFundsByMonth = socialFundService.getTotalSocialFundsByMonth(month);
        return new Response<>(totalSocialFundsByMonth);
    }

    @GraphQLQuery(name = "getTotalSocialFundsByMember", description = "Getting Group's total social funds by member")
    public Response<Double> getTotalSocialFundsByMember(@GraphQLArgument(name = "memberId") Long memberId, @GraphQLArgument(name = "year") Integer year) {
        Double totalSocialFundsByMember = socialFundService.getTotalSocialFundsByMember(memberId, year);
        return new Response<>(totalSocialFundsByMember);
    }

    @GraphQLMutation(name = "deleteSocialFund", description = "Deleting community fund - DELETE_COMMUNITY_FUND")
    public Response<SocialFund> deleteSocialFund(@GraphQLArgument(name = "socialFundId") Long id) {
        return socialFundService.deleteSocialFund(id);
    }
}
