package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.service.MemberService;
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
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GraphQLMutation(name = "saveMkobaMember", description = "Saving Mkoba member")
    public Response<Member> saveMkobaMember(@GraphQLArgument(name = "MemberDto") MemberDto memberDto) {
        return memberService.saveMkobaMember(memberDto);
    }

    @GraphQLQuery(name = "getMkobaMembers", description = "Getting a page of Mkoba members")
    public ResponsePage<Member> getMkobaMembers(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return memberService.getMkobaMembers(pageableParam!=null?pageableParam:new PageableParam());
    }


}
