package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.DividendDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.YearlyDividendRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import java.time.Month;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class YearlyDividendService {

    private final YearlyDividendRepository yearlyDividendRepository;

    private final MemberRepository memberRepository;

    public Response<YearlyDividend> saveYearlyDividend(DividendDto dto) {
        log.info("User is saving yearly dividend for memberUid");

        LocalDate now = LocalDate.now();
        if (now.getMonth() != Month.DECEMBER)
            return new Response<>("Withdrawals are only allowed in December.");

        // Validate input
        if (dto.getMemberId() == null) {
            return new Response<>("Member ID is required");
        }

        // Get member
        Optional<Member> member = memberRepository.findById(dto.getMemberId());
        if (member == null) {
            return new Response<>("Member not found");
        }

        if (dto.getWithdrawnAmount().compareTo(member.get().getMemberShares()) > 0) {
            return new Response<>("Withdrawn amount cannot exceed allocated amount");
        }

        BigDecimal withdrawnAmount = dto.getWithdrawnAmount() != null ? dto.getWithdrawnAmount() : BigDecimal.ZERO;

        // Create and populate YearlyDividend
        YearlyDividend dividend = new YearlyDividend();
        dividend.setMember(member.get());
        dividend.setYear(LocalDate.now().getYear()); // current year
        dividend.setAllocatedAmount(member.get().getMemberShares());
        dividend.setWithdrawnAmount(withdrawnAmount);

        // Save
        YearlyDividend saved = yearlyDividendRepository.save(dividend);
        log.info("Yearly Dividend saved with ID: " + saved.getId());

        return new Response<>(saved);
    }

    public ResponsePage<YearlyDividend> getYearlyDividendByMember(Long memberId, PageableParam pageableParam, Integer year) {
        log.info("Fetching yearly dividends for memberId" + memberId);
        return new ResponsePage<>(yearlyDividendRepository.findByMemberIdAndYear(memberId, pageableParam.getPageable(true), pageableParam.key(), year));
    }

    public Response<YearlyDividend> deleteYearlyDividend(Long id) {
        log.info("Deleting Yearly Dividend with id");

        if (id == null)
            return new Response<>("YearlyDividend ID is required");

        Optional<YearlyDividend> optional = yearlyDividendRepository.findById(id);
        if (optional.isEmpty())
            return new Response<>("YearlyDividend not found");
        YearlyDividend yearlyDividend = optional.get();

        try {
            yearlyDividend.delete();
            return new Response<>(yearlyDividendRepository.save(optional.get()));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            return Response.error(msg);
        }
    }

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

    public Double getTotalDividends(Integer year) {
        Double totalDividends = yearlyDividendRepository.getTotalDividends(year);
        return totalDividends != null ? totalDividends : 0.0;
    }
}







