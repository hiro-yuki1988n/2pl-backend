package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.SocialFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Month;
import java.util.Optional;

public interface SocialFundRepository extends JpaRepository<SocialFund, Long> {

    @Query("SELECT sf FROM SocialFund sf WHERE sf.member.id = :memberId AND sf.month = :month AND sf.isActive = true")
    Optional<SocialFund> findByMemberAndMonth(Long memberId, String month);

    @Query("SELECT sf FROM SocialFund sf WHERE lower(concat(sf.month, sf.id)) like %:key% and sf.month= :month AND sf.isActive = true")
    Page<SocialFund> getSocialFundsByMonth(Month month, Pageable pageable, String key);

    @Query("SELECT SUM(sf.amount) FROM SocialFund sf WHERE sf.isActive=true")
    Double getTotalSocialFunds();

    @Query("SELECT SUM(sf.amount) FROM SocialFund sf WHERE sf.isActive=true AND sf.month=:monthEnum")
    Double getTotalSocialFundsByMonth(Month monthEnum);

    @Query("SELECT SUM(sf.amount) FROM SocialFund sf WHERE sf.isActive=true AND sf.member.id=:memberId AND EXTRACT(YEAR FROM sf.createdAt) = :year")
    Double getTotalSocialFundsByMember(Long memberId, Integer year);

    @Query("SELECT sf FROM SocialFund sf WHERE lower(concat(sf.id, sf.member.name)) like %:key% and sf.isActive=true")
    Page<SocialFund> findAllSocialFunds(Pageable pageable, String key);

    @Query("SELECT sf FROM SocialFund sf WHERE sf.isActive=true AND sf.member.id=:memberId AND EXTRACT(YEAR FROM sf.createdAt) = :year")
    Page<SocialFund> getSocialFundsByMember(Pageable pageable, Long memberId, Integer year);
}
