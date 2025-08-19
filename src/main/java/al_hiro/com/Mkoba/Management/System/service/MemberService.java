package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.dto.RemoveMemberDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.SocialFund;
import al_hiro.com.Mkoba.Management.System.enums.ContributionCategory;
import al_hiro.com.Mkoba.Management.System.enums.RemoveReason;
import al_hiro.com.Mkoba.Management.System.repository.ContributionRepository;
import al_hiro.com.Mkoba.Management.System.repository.ExpendituresRepository;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.SocialFundRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Service
@Log
@RequiredArgsConstructor
public class MemberService {
    private final SocialFundRepository socialFundRepository;
    private final ContributionRepository contributionRepository;

    private final ExpendituresRepository expendituresRepository;

    private final MemberRepository memberRepository;

    private final ExpendituresService expendituresService;

    private final SocialFundService socialFundService;

    private final ContributionService contributionService;

    private final YearlyDividendService yearlyDividendService;

    private final CommonFundService commonFundService;

    public Response<Member> saveMkobaMember(MemberDto memberDto) {
        if (memberDto == null)
            return new Response<>("Data is required");
        Optional<Member> existingMember = memberRepository.findMemberByEmail(memberDto.getEmail());
        if (existingMember.isPresent() && !existingMember.get().getId().equals(memberDto.getId()))
            return new Response<>("Email already exists");

        Member member;
        if (memberDto.getId() != null) {
            Optional<Member> optionalMember = memberRepository.findById(memberDto.getId());
            if (optionalMember.isEmpty())
                return new Response<>("Mkoba member not found");
            member = optionalMember.get();
            member.update();
        } else member = new Member();

        if (memberDto.getName().isEmpty())
            return new Response<>("Name is required");
        if (memberDto.getEmail().isEmpty())
            return new Response<>("Email is required");
        if (memberDto.getPhone().isEmpty())
            return new Response<>("Phone is required");
        if (memberDto.getMemberRole() == null)
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
        } catch (Exception e) {
            e.printStackTrace();
            String msg = Utils.getExceptionMessage(e);
            if (msg.contains("name"))
                return new Response<>("Duplicate name");
            if (msg.contains("email"))
                return new Response<>("Duplicate email");
            if (msg.contains("phone"))
                return new Response<>("Duplicate phone");
            return new Response<>("Could not save Mkoba member");
        }
    }

    public ResponsePage<Member> getMkobaMembers(PageableParam pageableParam) {
        return new ResponsePage<>(memberRepository.getMkobaMembers(pageableParam.getPageable(true), pageableParam.key()));
    }

    public Response<Member> getMkobaMemberById(Long id) {
        if (id == null)
            return new Response<>("Member identity required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
            return new Response<>("Mkoba member not found");
        return new Response<>(optionalMember.get());
    }

    public Response<Member> deleteMkobaMember(Long id) {
        if (id == null)
            return new Response<>("Member identity required");
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
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
        Double entryFees = contributionService.getTotalEntryFees(ContributionCategory.ENTRY_FEE);
        Double groupExpenditures = expendituresService.getGroupExpenditures();
        Double groupSavings = (memberShares + socialFund + entryFees) - groupExpenditures;
        return groupSavings != null ? groupSavings : 0.0;
    }

    public Integer getTotalNumberOfMembers() {
        return (int) memberRepository.getTotalActiveMembers();
    }

    public Double getTotalMemberSharesByYear(Long memberId, Integer year) {
        log.info("Getting Total Member Shares for a current year");
        if (year == null) {
            year = Year.now().getValue();
        }
        Double totalMemberSharesByYear = memberRepository.getTotalMemberSharesByYear(memberId, year);
        return totalMemberSharesByYear != null ? totalMemberSharesByYear : 0.0;
    }

    public Response<String> uploadMemberPhoto(Long memberId, MultipartFile file) {
        log.info("Uploading member photo");
        if (memberId == null)
            return new Response<>("Member identity required");
        if (file == null)
            return new Response<>("Photo is required");

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty())
            return new Response<>("Mkoba member not found");
        Member member = optionalMember.get();
        try {
            // 1. Hakikisha ni image
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/"))
                return new Response<>("Invalid file type. Only images allowed.");

            // 2. Tengeneza jina la file
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "member_" + memberId + "_passport" + extension;

            // 3. Save file kwenye folder (e.g. uploads/)
            String uploadDir = "uploads/passports/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, file.getBytes());

            // 4. Set file name kwenye member entity
            member.setPassportPhoto(filename);
            memberRepository.save(member);

            return new Response<>("Photo uploaded successfully");

        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>("Failed to upload photo");
        }
    }

    public Response<String> getMemberPhoto(String filename) {
        log.info("Getting member photo");

        try {
            Path path = Paths.get("uploads/passports/" + filename);
            if (!Files.exists(path) || !Files.isReadable(path)) {
                return new Response<>("Photo not found");
            }

            byte[] imageBytes = Files.readAllBytes(path);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return new Response<>(base64Image); // success
        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>("Failed to load photo");
        }
    }

    public Response<Member> removeMember(RemoveMemberDto dto) {
        log.info("Removing member");

        if (dto == null || dto.getMemberId() == null) {
            return new Response<>("Member identity required");
        }

        Optional<Member> optionalMember = memberRepository.findById(dto.getMemberId());
        if (optionalMember.isEmpty()) {
            return new Response<>("Mkoba member not found");
        }

        Member member = optionalMember.get();
        member.setRemoved(true);
        member.setIsActive(false);
        member.setIsDeleted(true);
        member.setRemovedAt(LocalDate.now());
        member.setRemoveReason(dto.getRemoveReason());

        try {
            member = memberRepository.save(member);

            if (Boolean.TRUE.equals(member.getRemoved())) {
                BigDecimal shares = defaultToZero(member.getMemberShares());
                BigDecimal socialFunds = defaultToZero(socialFundRepository.getSocialFunds(member.getId()));

                BigDecimal memberTakeAway;
                BigDecimal leftOverShares = BigDecimal.ZERO;
                BigDecimal leftOverSocial = BigDecimal.ZERO;

                if (dto.getRemoveReason() == RemoveReason.death) {
                    memberTakeAway = shares.add(socialFunds);
                } else {
                    memberTakeAway = shares.add(socialFunds).multiply(BigDecimal.valueOf(0.5));
                    leftOverShares = shares.multiply(BigDecimal.valueOf(0.5));
                    leftOverSocial = socialFunds.multiply(BigDecimal.valueOf(0.5));
                }

                yearlyDividendService.saveYearlyDividend(member.getId(), memberTakeAway);

                if (leftOverShares.compareTo(BigDecimal.ZERO) > 0) {
                    commonFundService.generateCommonFund(leftOverShares);
                }

                if (leftOverSocial.compareTo(BigDecimal.ZERO) > 0) {
                    commonFundService.generateSocialFund(leftOverSocial);
                }

                List<SocialFund> memberSocialFunds = socialFundRepository.findMemberSocialFunds(dto.getMemberId());
                for (SocialFund funds : memberSocialFunds) {
                    funds.setIsActive(false);
                }
                socialFundRepository.saveAll(memberSocialFunds);
            }

            return new Response<>(member);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(Utils.getExceptionMessage(e));
        }
    }

    private BigDecimal defaultToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public ResponsePage<Member> getPastMembers(PageableParam pageableParam) {
        log.info("Getting past members");
        return new ResponsePage<>(memberRepository.getPastMembers(pageableParam.getPageable(true), pageableParam.key()));
    }

    public Response<Member> restoreMember(Long id) {
        log.info("Restoring member");
        if (id == null)
            return new Response<>("Member identity required");

        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty())
            return new Response<>("Mkoba member not found");

        Member member = optionalMember.get();
        member.setRemoved(false);
        member.setIsActive(true);
        member.setIsDeleted(false);
        member.setRemovedAt(null);
        member.setRemoveReason(null);

        try {
            member = memberRepository.save(member);
            return new Response<>(member);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(Utils.getExceptionMessage(e));
        }
    }
}
