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

import java.util.Map;

@Service
@GraphQLApi
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GraphQLMutation(name = "saveMkobaMember", description = "Saving Mkoba member")
    public Response<Member> saveMkobaMember(@GraphQLArgument(name = "memberDto") MemberDto memberDto) {
        return memberService.saveMkobaMember(memberDto);
    }

    @GraphQLQuery(name = "getMkobaMembers", description = "Getting a page of Mkoba members")
    public ResponsePage<Member> getMkobaMembers(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return memberService.getMkobaMembers(pageableParam!=null?pageableParam:new PageableParam());
    }

    @GraphQLQuery(name = "getMkobaMemberById", description = "Getting a Mkoba member by id")
    public Response<Member> getMkobaMemberById(@GraphQLArgument(name = "id") Long id) {
        return memberService.getMkobaMemberById(id);
    }

    @GraphQLMutation(name = "deleteMkobaMember", description = "Deleting a Mkoba member by id")
    public Response<Member> deleteMkobaMember(@GraphQLArgument(name = "id") Long id) {
        return memberService.deleteMkobaMember(id);
    }

    @GraphQLQuery(name = "getTotalNumberOfMembers", description = "Getting Total number of members")
    public Response<Integer> getTotalNumberOfMembers() {
        Integer members = memberService.getTotalNumberOfMembers();
        return new Response<>(members);
    }

    @GraphQLQuery(name = "getGroupSavings", description = "Getting Group savings")
    public Response<Double> getGroupSavings() {
        Double groupSavings = memberService.getGroupSavings();
        return new Response<>(groupSavings);
    }

    @GraphQLQuery(name = "getTotalMemberSharesByYear", description = "Getting Total Member Shares")
    public Response<Double> getTotalMemberSharesByYear(@GraphQLArgument(name = "memberId") Long memberId, @GraphQLArgument(name = "year") Integer year) {
        Double memberSharesByYear = memberService.getTotalMemberSharesByYear(memberId, year);
        return new Response<>(memberSharesByYear);
    }
}
