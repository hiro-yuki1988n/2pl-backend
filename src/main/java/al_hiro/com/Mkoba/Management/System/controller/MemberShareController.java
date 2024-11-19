package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.ContributionDto;
import al_hiro.com.Mkoba.Management.System.dto.MemberShareDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.entity.MemberShare;
import al_hiro.com.Mkoba.Management.System.service.MemberShareService;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
@GraphQLApi
@RequiredArgsConstructor
public class MemberShareController {
    @Autowired
    private MemberShareService memberShareService;

    @GraphQLMutation(name = "saveMemberShare", description = "Save Member Share - SAVE_MEMBER_SHARE")
    public Response<MemberShare> saveMemberShare(@GraphQLArgument(name = "MemberShareDto") MemberShareDto memberShareDto) {
        return memberShareService.saveMemberShare(memberShareDto);
    }

    @GraphQLQuery(name = "getTotalGroupFunds", description = "Get total group funds (shares + loan profits + penalties)")
    public Response<Double> getTotalGroupFunds() {
        Double totalFunds = memberShareService.calculateTotalGroupFunds();
        return new Response<>(totalFunds);
    }
}

