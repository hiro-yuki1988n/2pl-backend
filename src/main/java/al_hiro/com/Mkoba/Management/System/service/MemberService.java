package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Response<Member> saveMkobaMember(MemberDto memberDto) {
//        log.info("User with email " + LoggedUser.getEmail() + " is saving a Mkoba member");
        if(memberDto == null)
            return new Response<>("Data is required");
        Optional<Member> existingMember = memberRepository.findMemberByEmail(memberDto.getEmail());
        if(existingMember.isPresent())
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
        if(memberDto.getPassword().isEmpty())
            return new Response<>("Password is required");
        if(memberDto.getMemberRole() == null)
            return new Response<>("Member role is required");

        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setPassword(memberDto.getPassword());
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
}
