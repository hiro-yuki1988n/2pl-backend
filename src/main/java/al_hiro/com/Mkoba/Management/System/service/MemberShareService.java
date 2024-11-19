package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.MemberShareDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.MemberShare;
import al_hiro.com.Mkoba.Management.System.repository.ContributionRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberShareRepository;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@GraphQLApi
@Log
public class MemberShareService {

    @Autowired
    private MemberShareRepository memberShareRepository;

    @Autowired
    private LoanService loanService;
    
    @Autowired
    private ContributionRepository contributionRepository;

    public Response<MemberShare> saveMemberShare(MemberShareDto memberShareDto) {

        log.info("Saving Member's Share");
        if (memberShareDto == null)
            return Response.warning(null, "Member's Share is required");

        MemberShare memberShare;
        if (memberShareDto.getId() != null) {
            Optional<MemberShare> optionalMemberShare = memberShareRepository.findById(memberShareDto.getId());
            if (optionalMemberShare.isEmpty())
                return Response.warning(null, "Member's Share not found");
            memberShare = optionalMemberShare.get();
            memberShare.update();
        } else {
            if (memberShareRepository.findByMemberIdAndAmount(memberShareDto.getMemberId(), memberShareDto.getAmount()).isPresent())
                return Response.warning(null, "Member's Share for this member already exists");
            memberShare = new MemberShare();
        }

        if (memberShareDto.getId() == null)
            return Response.warning(null, "Member Share Id is required");
        if (memberShareDto.getAmount() == null)
            return Response.warning(null, "Amount paid is required");
        if (memberShareDto.getMemberId() == null)
            return Response.warning(null, "Member Id is required");
        if (memberShareDto.getDescription() == null)
            return Response.warning(null, "Member's Description is required");

        memberShare.setAmount(memberShareDto.getAmount());
        memberShare.setId(memberShareDto.getId());
        memberShare.setDescription(memberShareDto.getDescription());
        memberShare.setMember(Utils.entity(Member.class, memberShareDto.getMemberId()));


        // Save Member's Share to a database
        try {
            memberShareRepository.save(memberShare);
            return new Response<>(memberShare);
        } catch (Exception e) {
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if (message.contains("description"))
                return new Response<>("Invalid Description");
            if (message.contains("(memberId)"))
                return new Response<>("Invalid Member");
            if (message.contains("amount"))
                return new Response<>("Invalid Amount");
            return new Response<>("Could not save Share for a member");
        }
    }

    public Double calculateTotalGroupFunds() {
        Double totalMemberShares = memberShareRepository.sumAllShares();

        // Jumla ya faida za mikopo
        Double totalLoanProfits = loanService.getGroupLoansProfit();

        // Jumla ya penalties
        Double totalPenalties = loanService.getGroupTotalPenalties();

        // Jumla ya hela ya kikundi
        return (totalMemberShares != null ? totalMemberShares : 0.0) +
                (totalLoanProfits != null ? totalLoanProfits : 0.0) +
                (totalPenalties != null ? totalPenalties : 0.0);
    }
}


