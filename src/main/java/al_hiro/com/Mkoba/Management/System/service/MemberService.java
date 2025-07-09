package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.repository.ExpendituresRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class MemberService {

    private final ExpendituresRepository expendituresRepository;

    private final MemberRepository memberRepository;

    private final ExpendituresService expendituresService;

    private final SocialFundService socialFundService;

    public Response<Member> saveMkobaMember(MemberDto memberDto) {
        if(memberDto == null)
            return new Response<>("Data is required");
        Optional<Member> existingMember = memberRepository.findMemberByEmail(memberDto.getEmail());
        if(existingMember.isPresent() && !existingMember.get().getId().equals(memberDto.getId()))
            return new Response<>("Email already exists");

        Member member;
        if(memberDto.getId()!=null){
            Optional<Member> optionalMember = memberRepository.findById(memberDto.getId());
            if(optionalMember.isEmpty())
                return new Response<>("Mkoba member not found");
            member = optionalMember.get();
            member.update();
        } else member = new Member();

        if(memberDto.getName().isEmpty())
            return new Response<>("Name is required");
        if(memberDto.getEmail().isEmpty())
            return new Response<>("Email is required");
        if(memberDto.getPhone().isEmpty())
            return new Response<>("Phone is required");
        if(memberDto.getMemberRole() == null)
            return new Response<>("Member role is required");

        member.setName(memberDto.getName());
        member.setGender(memberDto.getGender());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setJoiningDate(memberDto.getJoiningDate());
        member.setMemberRole(memberDto.getMemberRole());
        try {
            memberRepository.save(member);
            return new Response<>(member);
        }catch (Exception e){
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            if(msg.contains("name"))
                return new Response<>("Duplicate name");
            if(msg.contains("email"))
                return new Response<>("Duplicate email");
            if(msg.contains("phone"))
                return new Response<>("Duplicate phone");
            return new Response<>("Could not save Mkoba member");
        }
    }

    public ResponsePage<Member> getMkobaMembers(PageableParam pageableParam) {
        return new ResponsePage<>(memberRepository.getMkobaMembers(pageableParam.getPageable(true), pageableParam.key()));
    }

    public Response<Member> getMkobaMemberById(Long id) {
        if(id == null)
            return new Response<>("Member identity required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(optionalMember.isEmpty())
            return new Response<>("Mkoba member not found");
        return new Response<>(optionalMember.get());
    }

    public Response<Member> deleteMkobaMember(Long id) {
        if(id == null)
            return new Response<>("Member identity required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(optionalMember.isEmpty())
            return new Response<>("Mkoba member not found");
        Member member = optionalMember.get();
        try {
            member.delete();
            member = memberRepository.save(member);
            return new Response<>(member);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            return Response.error(msg);
        }
    }

    public Double getGroupSavings() {
        Double memberShares = memberRepository.getGroupSavings();
        Double socialFund = socialFundService.getTotalSocialFunds();
        Double groupExpenditures = expendituresService.calculateTotalExpenditures();
        Double groupSavings = (memberShares + socialFund)-groupExpenditures;
        return  groupSavings!=null ? groupSavings:0.0;
    }

    public Integer getTotalNumberOfMembers() {
        return (int) memberRepository.count();
    }

    public Double getTotalMemberSharesByYear(Long memberId, Integer year) {
        log.info("Getting Total Member Shares for a current year");
        if (year == null) {
            year = Year.now().getValue();
        }
        Double totalMemberSharesByYear = memberRepository.getTotalMemberSharesByYear(memberId, year);
        return totalMemberSharesByYear != null ? totalMemberSharesByYear : 0.0;
    }
}
