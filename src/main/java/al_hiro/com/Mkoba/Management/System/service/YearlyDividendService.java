package al_hiro.com.Mkoba.Management.System.service;
import al_hiro.com.Mkoba.Management.System.dto.DividendDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.YearlyDividendRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

        // Validate input
        if (dto.getMemberId() == null || dto.getMemberUid().isBlank()) {
            return new Response<>("Member UID is required");
        }

        if (dto.getAllocatedAmount() == null || dto.getAllocatedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new Response<>("Allocated amount must be greater than 0");
        }

        // Get member
        Optional<Member> member = memberRepository.findById(dto.getMemberId());
        if (member == null) {
            return new Response<>("Member not found");
        }

        BigDecimal withdrawnAmount = dto.getWithdrawnAmount() != null ? dto.getWithdrawnAmount() : BigDecimal.ZERO;

        if (withdrawnAmount.compareTo(dto.getAllocatedAmount()) > 0) {
            return new Response<>("Withdrawn amount cannot exceed allocated amount");
        }

        // Create and populate YearlyDividend
        YearlyDividend dividend = new YearlyDividend();
        dividend.setMember(member.get());
        dividend.setYear(LocalDate.now().getYear()); // current year
        dividend.setAllocatedAmount(dto.getAllocatedAmount());
        dividend.setWithdrawnAmount(withdrawnAmount);
        dividend.setRemainingBalance(dto.getAllocatedAmount().subtract(withdrawnAmount));
        dividend.setApproved(false); // not approved during initial save
        dividend.setApprovedAt(null); // no approval timestamp yet

        // Save
        YearlyDividend saved = yearlyDividendRepository.save(dividend);
        log.info("Yearly Dividend saved with ID");

        return new Response<>("Saved successfully");
    }


    public ResponsePage<YearlyDividend> getYearlyDividendByMember(Long memberId, PageableParam pageableParam, Integer year) {
        log.info("Fetching yearly dividends for memberId");

        if (memberId == null) {
            return new ResponsePage<>("Member ID is required");
        }

        boolean memberExists = memberRepository.existsById(memberId);
        if (!memberExists) {
            return new ResponsePage<>("Member not found");
        }

        Pageable pageable = PageRequest.of(
                pageableParam != null ? pageableParam.getPage() : 0,
                pageableParam != null ? pageableParam.getSize() : 10,
                Sort.by("year").descending()
        );

        Page<YearlyDividend> page = (year != null)
                ? yearlyDividendRepository.findByMemberIdAndYear(memberId, year, pageable)
                : yearlyDividendRepository.findByMemberId(memberId, pageable);

        return new ResponsePage<>("Fetched successfully");
    }

    public Response<YearlyDividend> deleteYearlyDividend(Long id) {
        log.info("Deleting Yearly Dividend with id");

        if (id == null) {
            return new Response<>("YearlyDividend ID is required");
        }

        Optional<YearlyDividend> optional = yearlyDividendRepository.findById(id);
        if (optional.isEmpty()) {
            return new Response<>("YearlyDividend not found");
        }

        yearlyDividendRepository.deleteById(id);
        log.info("Yearly Dividend deleted with id");


        return new Response<>("Deleted successfully");
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

}







