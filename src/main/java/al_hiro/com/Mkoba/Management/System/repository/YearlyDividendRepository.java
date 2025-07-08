package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface YearlyDividendRepository extends JpaRepository<YearlyDividend, Long> {
<<<<<<< HEAD
    Page<YearlyDividend> findByMemberIdAndYear(Long memberId, Integer year, Pageable pageable);

    Page<YearlyDividend> findByMemberId(Long memberId, Pageable pageable);
=======

    @Query("SELECT yd FROM YearlyDividend yd WHERE lower(concat(yd.member.name, yd.id, yd.year)) like %:key% and yd.isActive=true ")
    Page<YearlyDividend> findYearlyDividends(Pageable pageable, String key);
>>>>>>> adfba08bff30ef050bb804bb0e2058353dd368bf
}
