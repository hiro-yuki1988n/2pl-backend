package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.YearlyDividendRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class YearlyDividendService {

    private final YearlyDividendRepository yearlyDividendRepository;
    private final MemberRepository memberRepository;


    public Response<YearlyDividend> approveDividend(Long yearlyDividendId) {
        log.info("Approving member's yearly dividend ");
        Optional<YearlyDividend> optionalYearlyDividend = yearlyDividendRepository.findById(yearlyDividendId);
        if (optionalYearlyDividend.isEmpty())
            return new Response<>("No yearly dividend provided");
        YearlyDividend dividend = optionalYearlyDividend.get();
        dividend.setApproved(true);
        dividend.setApprovedAt(LocalDateTime.now());

        BigDecimal remainingBalance;
        Optional<Member> oMember = memberRepository.findById(optionalYearlyDividend.get().getMember().getId());
        if (dividend.getApproved()) {
            remainingBalance = dividend.getAllocatedAmount().subtract(dividend.getWithdrawnAmount());
            dividend.setRemainingBalance(remainingBalance);
            oMember.get().setMemberShares(remainingBalance);
            memberRepository.save(oMember.get());
        }
        try {
            dividend = yearlyDividendRepository.save(dividend);
            return new Response<>(dividend);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("Cannot approve Yearly dividend");
        }
    }

    public ResponsePage<YearlyDividend> getYearlyDividends(PageableParam pageableParam) {
        log.info("Getting list of members' yearly dividends");
        return new ResponsePage<>(yearlyDividendRepository.findYearlyDividends(pageableParam.getPageable(true), pageableParam.key()));
    }
}
