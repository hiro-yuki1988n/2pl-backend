package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.MemberShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberShareRepository extends JpaRepository<MemberShare,Long> {
    @Query("SELECT m FROM MemberShare m WHERE m.member.id = :memberId AND m.amount = :amount AND m.isActive = true")
    Optional<MemberShare> findByMemberIdAndAmount(Long memberId, Double amount);

    @Query("SELECT SUM(ms.amount) FROM MemberShare ms")
    Double sumAllShares();
}
