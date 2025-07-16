package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.SocialFundDto;
import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.SocialFund;
import al_hiro.com.Mkoba.Management.System.repository.CommonFundRepository;
import al_hiro.com.Mkoba.Management.System.repository.ExpendituresRepository;
import al_hiro.com.Mkoba.Management.System.repository.SocialFundRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class SocialFundService {
    private final CommonFundRepository commonFundRepository;

    private final SocialFundRepository socialFundRepository;
    private final ExpendituresRepository expendituresRepository;

    public Response<SocialFund> saveSocialFund(SocialFundDto socialFundDto) {
        log.info("Saving social fund");
        if (socialFundDto == null)
            return Response.warning(null, "Social fund is required");

        SocialFund socialFund;
        if (socialFundDto.getId() != null) {
            Optional<SocialFund> optionalSocialFund = socialFundRepository.findById(socialFundDto.getId());
            if (optionalSocialFund.isPresent()) {
                socialFund = optionalSocialFund.get();
                if (socialFund.getMember().getId().equals(socialFundDto.getMemberId()) && socialFund.getMonth().equals(socialFundDto.getMonth())) {
                    return Response.warning(null, "Social fund for this member and month already exists");
                }
                socialFund.update();
            } else {
                socialFund = new SocialFund();
            }
        } else {
            socialFund = new SocialFund();
        }

        if (socialFundDto.getAmount() == null)
            return Response.warning(null, "Amount paid is required");
        if (socialFundDto.getMonth() == null)
            return Response.warning(null, "Month paid for is required");
        if (socialFundDto.getMemberId() == null)
            return Response.warning(null, "Member who paid is required");


        socialFund.setAmount(socialFundDto.getAmount());
        socialFund.setMonth(Month.valueOf(socialFundDto.getMonth()));
        socialFund.setMember(Utils.entity(Member.class, socialFundDto.getMemberId()));
        socialFund.setPaymentDate(LocalDateTime.now());

        try {
            socialFundRepository.save(socialFund);
            return new Response<>(socialFund);
        } catch (Exception e) {
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if (message.contains("month"))
                return new Response<>("Invalid Month");
            if (message.contains("(memberId)"))
                return new Response<>("Invalid Member");
            if (message.contains("amount"))
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

        Double leftOverFunds = commonFundRepository.findLeftOverFunds();
        Double socialExpense = expendituresRepository.getSocialExpenses();
        Double totalSocial = socialFundRepository.getTotalSocialFunds();

        // Default to 0.0 if any are null
        leftOverFunds = leftOverFunds != null ? leftOverFunds : 0.0;
        socialExpense = socialExpense != null ? socialExpense : 0.0;
        totalSocial = totalSocial != null ? totalSocial : 0.0;

        return (leftOverFunds + totalSocial) - socialExpense;
    }

    public Double getTotalSocialFundsByMonth(String month) {
        log.info("Getting total social fund for a month");
        Double totalSocialFundsByMonth = socialFundRepository.getTotalSocialFundsByMonth(Month.valueOf(month.toUpperCase()));
        return totalSocialFundsByMonth != null ? totalSocialFundsByMonth : 0.0;
    }

    public Double getTotalSocialFundsByMember(Long memberId, Integer year) {
        log.info("Getting total social fund for a member");
        Double totalSocialFundsByMember = socialFundRepository.getTotalSocialFundsByMember(memberId, year);
        return totalSocialFundsByMember != null ? totalSocialFundsByMember : 0.0;
    }

    public ResponsePage<SocialFund> getAllSocialFunds(PageableParam pageableParam) {
        log.info("Getting all Social Funds");
        return new ResponsePage<>(socialFundRepository.findAllSocialFunds(pageableParam.getPageable(true), pageableParam.key()));
    }

    public ResponsePage<SocialFund> getSocialFundsByMember(PageableParam pageableParam, Long memberId, Integer year) {
        log.info("Getting Social Funds by Member");
        return new ResponsePage<>(socialFundRepository.getSocialFundsByMember(pageableParam.getPageable(true), memberId, year));
    }

    public Response<SocialFund> deleteSocialFund(Long id) {
        log.info("Deleting Social Fund");
        if (id == null)
            return Response.warning(null, "Social Fund ID is required");
        Optional<SocialFund> optionalSocialFund = socialFundRepository.findById(id);
        if (optionalSocialFund.isEmpty())
            return Response.warning(null, "Contribution not found");
        SocialFund socialFund = optionalSocialFund.get();
        socialFund.delete();
        try {
            return new Response<>(socialFundRepository.save(socialFund));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Could not delete monthly Social Fund for a member");
        }
    }
}
