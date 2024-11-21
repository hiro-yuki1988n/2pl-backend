package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Contribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    @Query("SELECT c FROM Contribution c WHERE c.member.id = :memberId AND c.month = :month AND c.isActive = true")
    Optional<Contribution> findByMemberAndMonth(Long memberId, String month);

    @Query("SELECT c FROM Contribution c WHERE LOWER(CONCAT(c.month)) LIKE %:key% AND (:isActive is null OR c.isActive =: isActive)")
    Page<Contribution> getContributions(Pageable pageable, Boolean isActive, String key);

    @Query("SELECT c FROM Contribution c WHERE (:isActive is null OR c.isActive =:isActive) AND LOWER(CONCAT(c.month)) LIKE %:key% AND c.member.id = :id")
    Page<Contribution> getAllByMember(Pageable pageable, Boolean isActive, String key, Long id);

    @Query("SELECT c FROM Contribution c WHERE (:isActive is null OR c.isActive =:isActive) AND c.month = :month")
    Page<Contribution> getAllByMonth(Pageable pageable, Boolean isActive, String month);

    @Query("SELECT SUM(c.amount) FROM Contribution c WHERE c.isActive=true")
    Double getTotalContributions();

    @Query("SELECT SUM(c.amount) FROM Contribution c WHERE c.member.id = :memberId AND c.isActive=true")
    Double getTotalContributionsByMember(Long memberId);
}
