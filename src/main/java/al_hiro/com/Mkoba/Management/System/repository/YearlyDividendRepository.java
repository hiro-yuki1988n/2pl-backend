package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.YearlyDividend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearlyDividendRepository extends JpaRepository<YearlyDividend, Long> {
    Page<YearlyDividend> findByMemberIdAndYear(Long memberId, Integer year, Pageable pageable);

    Page<YearlyDividend> findByMemberId(Long memberId, Pageable pageable);
}
