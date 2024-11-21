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

@Service
@GraphQLApi
public class SocialFundController {

    @Autowired
    private SocialFundService socialFundService;

    @GraphQLMutation(name = "saveSocialFund", description = "Saving Social Fund - SAVE_SOCIAL_FUND")
    public Response<SocialFund> saveSocialFund(@GraphQLArgument(name = "socialFundDto") SocialFundDto socialFundDto) {
        return socialFundService.saveSocialFund(socialFundDto);
    }

    @GraphQLQuery(name = "getSocialFundsByMonth", description = "Getting a list of Social funds by month")
    public ResponsePage<SocialFund> getSocialFundsByMonth(@GraphQLArgument(name = "pageableParam")PageableParam pageableParam, @GraphQLArgument(name = "month") Month month){
        return socialFundService.getSocialFundsByMonth(month, pageableParam!=null?pageableParam:new PageableParam());
    }

    @GraphQLQuery(name = "getTotalSocialFunds", description = "Getting Group's total social fund")
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
    public Response<Double> getTotalSocialFundsByMember(@GraphQLArgument(name = "memberId") Long memberId) {
        Double totalSocialFundsByMember = socialFundService.getTotalSocialFundsByMember(memberId);
        return new Response<>(totalSocialFundsByMember);
    }
}
