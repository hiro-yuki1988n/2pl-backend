package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.CommonFundDto;
import al_hiro.com.Mkoba.Management.System.entity.CommonFund;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import al_hiro.com.Mkoba.Management.System.enums.SourceType;
import al_hiro.com.Mkoba.Management.System.repository.CommonFundRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class CommonFundService {

    private final CommonFundRepository commonFundRepository;
    private final MemberRepository memberRepository;

    public Response<CommonFund> saveCommonFund(CommonFundDto commonFundDto) {
        log.info("Saving common fund");
        if (commonFundDto==null)
            return Response.error("Common fund cannot be null");

        CommonFund commonFund;
        if (commonFundDto.getId()!=null) {
            Optional<CommonFund> optionalCommonFund = commonFundRepository.findById(commonFundDto.getId());
            if (optionalCommonFund.isEmpty())
                return new Response<>("Invalid common fund");
            commonFund = optionalCommonFund.get();
            commonFund.update();
        } else {
            commonFund = new CommonFund();
        }

        if (commonFundDto.getAmount()==null)
            return Response.error("Amount cannot be null");
        if (commonFundDto.getSourceType()==null)
            return Response.error("Fund type cannot be null");
        if (commonFundDto.getEntryDate()==null)
            return new Response<>("Entry date cannot be null");

        commonFund.setAmount(commonFundDto.getAmount());
        commonFund.setSourceType(commonFundDto.getSourceType());
        commonFund.setEntryDate(commonFundDto.getEntryDate());
        commonFund.setDescription(commonFundDto.getDescription());
        try {
            commonFundRepository.save(commonFund);

            if (commonFundDto.getSourceType().equals(SourceType.member_left_over) || commonFundDto.getSourceType().equals(SourceType.interest) || commonFundDto.getSourceType().equals(SourceType.penalty) ){

                List<Member> members = memberRepository.findAllAndActive(); // Fetch all group members

                // Calculate total group savings using BigDecimal
                BigDecimal groupSavings = members.stream()
                        .map(Member::getMemberShares)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (groupSavings.compareTo(BigDecimal.ZERO) == 0) {
                    return new Response<>("Total shares cannot be zero");
                }

                for (Member member : members) {
                    BigDecimal memberShares = member.getMemberShares();
                    if (memberShares == null || memberShares.compareTo(BigDecimal.ZERO) < 0) {
                        memberShares = BigDecimal.ZERO;
                    }

                    // Calculate member's share percentage
                    BigDecimal memberSharePercentage = memberShares.divide(groupSavings, 4, RoundingMode.HALF_UP);

                    // Calculate member's interest
                    BigDecimal memberInterest = commonFundDto.getAmount().multiply(memberSharePercentage);

                    // Update member's shares
                    BigDecimal updatedShares = memberShares.add(memberInterest);
                    member.setMemberShares(updatedShares);
                    memberRepository.save(member); // Save updated member shares
                }
            } else {
                return new Response("Its a social fund");
            }
            return new Response(commonFund);
        } catch (Exception e){
            e.printStackTrace();
            return Response.error("Error saving common fund");
        }
    }

    public ResponsePage<CommonFund> getCommonFunds(PageableParam pageableParam) {
        log.info("Getting common funds");
        return new ResponsePage<>(commonFundRepository.findCommonFunds(pageableParam.getPageable(true), pageableParam.key()));
    }

    public Response<CommonFund> getCommonFund(Long id) {
        log.info("Getting common fund");
        if (id==null)
            return Response.error("Id cannot be null");
        Optional<CommonFund> optionalCommonFund = commonFundRepository.findById(id);
        if (optionalCommonFund.isEmpty())
            return Response.error("Invalid common fund");
        return new Response(optionalCommonFund.get());
    }

    public Response<String> deleteCommonFund(Long id) {
        log.info("Deleting common fund");
        if (id==null)
            return Response.error("Id cannot be null");
        Optional<CommonFund> optionalCommonFund = commonFundRepository.findById(id);
        if (optionalCommonFund.isEmpty())
            return Response.error("Invalid common fund");
        optionalCommonFund.get();
        optionalCommonFund.get().delete();
        try {
            commonFundRepository.save(optionalCommonFund.get());
            return new Response(optionalCommonFund.get());
        }catch (Exception e){
            e.printStackTrace();
            return Response.error("Error deleting common fund");
        }
    }

    public Response<CommonFund> generateCommonFund(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Response<>("Invalid left over amount");
        }

        CommonFund fund = new CommonFund();
        fund.setAmount(amount);
        fund.setSourceType(SourceType.member_left_over);
        fund.setEntryDate(LocalDate.now());
        fund.setDescription("Member left over fund");

        try {
            fund = commonFundRepository.save(fund);

            if (List.of(SourceType.member_left_over, SourceType.interest, SourceType.penalty).contains(fund.getSourceType())) {
                List<Member> members = memberRepository.findAllAndActive();

                BigDecimal totalShares = members.stream()
                        .map(Member::getMemberShares)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalShares.compareTo(BigDecimal.ZERO) == 0) {
                    return new Response<>("Total shares cannot be zero");
                }

                for (Member m : members) {
                    BigDecimal memberShares = defaultToZero(m.getMemberShares());
                    BigDecimal percentage = memberShares.divide(totalShares, 4, RoundingMode.HALF_UP);
                    BigDecimal gain = amount.multiply(percentage);
                    m.setMemberShares(memberShares.add(gain));
                }
                memberRepository.saveAll(members);
            }

            return new Response<>(fund);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Error saving common fund");
        }
    }

    public Response<CommonFund> generateSocialFund(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Response<>("Invalid left over social fund");
        }

        CommonFund fund = new CommonFund();
        fund.setAmount(amount);
        fund.setSourceType(SourceType.social_fund);
        fund.setEntryDate(LocalDate.now());
        fund.setDescription("Member left over social fund");

        try {
            fund = commonFundRepository.save(fund);
            return new Response<>(fund);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Error saving common social fund");
        }
    }

    private BigDecimal defaultToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

}
