package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface YearlyDividendRepository extends JpaRepository<YearlyDividend, Long> {

    @Query("SELECT yd FROM YearlyDividend yd WHERE lower(concat(yd.id, yd.member.name)) like %:key% and yd.isActive=true AND yd.member.id = :memberId AND yd.year = :year")
    Page<YearlyDividend> findByMemberIdAndYear(Long memberId, Pageable pageable, String key, Integer year);

    @Query("SELECT yd FROM YearlyDividend yd WHERE lower(concat(yd.member.name, yd.id, yd.year)) like %:key% and yd.isActive=true ")
    Page<YearlyDividend> findYearlyDividends(Pageable pageable, String key);

    @Query("SELECT SUM(yd.withdrawnAmount) FROM YearlyDividend yd WHERE yd.year =:year AND yd.isActive=true")
    Double getTotalDividends(Integer year);

    List<YearlyDividend> findAllByMemberIdAndApprovedTrue(Long id);
}