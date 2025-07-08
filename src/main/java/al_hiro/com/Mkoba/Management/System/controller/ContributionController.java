package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.ContributionDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.service.ContributionService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.Month;

@Controller
@GraphQLApi
@RequiredArgsConstructor
public class ContributionController {

    private final ContributionService contributionService;

    @GraphQLMutation(name = "saveContribution", description = "Save contribution - SAVE_CONTRIBUTION")
    public Response<Contribution> saveContribution(@GraphQLArgument(name = "contributionDto") ContributionDto contributionDto) {
        return contributionService.saveContribution(contributionDto);
    }

    @GraphQLQuery(name = "getContributions", description = "Get all contributions - VIEW_ALL_CONTRIBUTIONS")
    public ResponsePage<Contribution> getContributions(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return contributionService.getContributions(pageableParam != null ? pageableParam : new PageableParam());
    }

    @GraphQLQuery(name = "getContribution", description = "Get one contribution - VIEW_ONE_CONTRIBUTION")
    public Response<Contribution> getContribution(@GraphQLArgument(name = "contributionId") Long id) {
        return contributionService.getContribution(id);
    }

    @GraphQLQuery(name = "getContributionsByMember", description = "Get contributions by Member - VIEW_MEMBER_CONTRIBUTIONS")
    public ResponsePage<Contribution> getContributionsByMember(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                               @GraphQLArgument(name = "memberId") Long id,
                                                               @GraphQLArgument(name = "year") Integer year) {
        return contributionService.getContributionsByMember(pageableParam != null ? pageableParam : new PageableParam(), id, year);
    }

    @GraphQLQuery(name = "getMonthlyContributions", description = "Get monthly contributions - VIEW_MONTHLY_CONTRIBUTIONS")
    public ResponsePage<Contribution> getMonthlyContributions(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                              @GraphQLArgument(name = "month") String month) {
        return contributionService.getMonthlyContributions(pageableParam != null ? pageableParam : new PageableParam(), month);
    }

    @GraphQLMutation(name = "deleteContribution", description = "Delete contribution - DELETE_CONTRIBUTION")
    public Response<Contribution> deleteContribution(@GraphQLArgument(name = "contributionId") Long id) {
        return contributionService.deleteContribution(id);
    }

    @GraphQLQuery(name = "getTotalContributions", description = "Getting Group's total member contributions")
    public Response<Double> getTotalContributions() {
        Double totalContributions = contributionService.getTotalContributions();
        return new Response<>(totalContributions);
    }

    @GraphQLQuery(name = "getTotalMemberContributions", description = "Getting total member contributions")
    public Response<Double> getTotalMemberContributions(@GraphQLArgument(name = "memberId") Long memberId, @GraphQLArgument(name = "year") Integer year) {
        Double totalMemberContributions = contributionService.getTotalMemberContributions(memberId, year);
        return new Response<>(totalMemberContributions);
    }

    @GraphQLQuery(name = "getContributionTotalPenalties", description = "Getting Group's profit from contribution penalties")
    public Response<Double> getContributionTotalPenalties(@GraphQLArgument(name = "month") Month month,@GraphQLArgument(name = "year") Integer year) {
        Double contributionTotalPenalties = contributionService.getContributionTotalPenalties(month, year);
        return new Response<>(contributionTotalPenalties);
    }

    @GraphQLQuery(name = "getTotalContributionsByMonthAndYear", description = "Getting Group's total member contributions")
    public Response<Double> getTotalContributionsByMonthAndYear(@GraphQLArgument(name = "month") Month month,
                                                                @GraphQLArgument(name = "year") Integer year) {
        Double totalContributionsByMonthAndYear = contributionService.getTotalContributionsByMonthAndYear(month, year);
        return new Response<>(totalContributionsByMonthAndYear);
    }

    @GraphQLQuery(name = "getLateContributionsByMember", description = "Get late contributions by Member - VIEW_MEMBER_CONTRIBUTIONS")
    public ResponsePage<Contribution> getLateContributionsByMember(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam,
                                                               @GraphQLArgument(name = "memberId") Long id,
                                                               @GraphQLArgument(name = "year") Integer year) {
        return contributionService.getLateContributionsByMember(pageableParam != null ? pageableParam : new PageableParam(), id, year);
    }
}
