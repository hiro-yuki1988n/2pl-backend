package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.SocialFundDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.SocialFund;
import al_hiro.com.Mkoba.Management.System.repository.SocialFundRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

@Service
@Log
public class SocialFundService {

    @Autowired
    private SocialFundRepository socialFundRepository;

    public Response<SocialFund> saveSocialFund(SocialFundDto socialFundDto) {
        log.info("Saving social fund");
        if (socialFundDto == null)
            return Response.warning(null,"Social fund is required");

        SocialFund socialFund;
        if (socialFundDto.getId() != null) {
            Optional<SocialFund> optionalSocialFund = socialFundRepository.findById(socialFundDto.getId());
            if (optionalSocialFund.isEmpty())
                return Response.warning(null,"Social fund not found");
            socialFund = optionalSocialFund.get();
            socialFund.update();
        } else{
//            if(socialFundRepository.findByMemberAndMonth(socialFundDto.getMemberId(), socialFundDto.getMonth()).isPresent())
//                return Response.warning(null,"Social fund for this member and month already exists");
            socialFund = new SocialFund();
        }

        if (socialFundDto.getAmount() == null)
            return Response.warning(null,"Amount paid is required");
        if (socialFundDto.getMonth() == null)
            return Response.warning(null,"Month paid for is required");
        if (socialFundDto.getMemberId() == null)
            return Response.warning(null,"Member who paid is required");

        socialFund.setAmount(socialFundDto.getAmount());
        socialFund.setMonth(socialFundDto.getMonth());
        socialFund.setMember(Utils.entity(Member.class, socialFundDto.getMemberId()));
        socialFund.setPaymentDate(LocalDateTime.now());

        // Save contribution to database
        try{
            socialFundRepository.save(socialFund);
            return new Response<>(socialFund);
        } catch (Exception e){
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if(message.contains("month"))
                return new Response<>("Invalid Month");
            if(message.contains("(memberId)"))
                return new Response<>("Invalid Member");
            if(message.contains("amount"))
                return new Response<>("Invalid Amount");
            return new Response<>("Could not save monthly social fund for a member");
        }
    }

    public ResponsePage<SocialFund> getSocialFundsByMonth(Month month, PageableParam pageableParam) {
        log.info("Getting Social Funds by Month");
        return new ResponsePage<>(socialFundRepository.getSocialFundsByMonth(month, pageableParam.getPageable(true), pageableParam.key()));
    }

    public Double getTotalSocialFunds() {
        log.info("Getting Group's total social fund");
        Double totalSocialFunds = socialFundRepository.getTotalSocialFunds();
        return  totalSocialFunds!=null ? totalSocialFunds:0.0;
    }

    public Double getTotalSocialFundsByMonth(String month) {
        log.info("Getting total social fund for a month");
        Double totalSocialFundsByMonth = socialFundRepository.getTotalSocialFundsByMonth(Month.valueOf(month.toUpperCase()));
        return  totalSocialFundsByMonth!=null? totalSocialFundsByMonth:0.0;
    }

    public Double getTotalSocialFundsByMember(Long memberId) {
        log.info("Getting total social fund for a member");
        Double totalSocialFundsByMember = socialFundRepository.getTotalSocialFundsByMember(memberId);
        return  totalSocialFundsByMember!=null? totalSocialFundsByMember:0.0;
    }
}
