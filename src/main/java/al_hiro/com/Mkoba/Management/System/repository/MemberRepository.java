package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email=:email")
    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m WHERE lower(concat(m.id, m.name)) like %:key% AND m.isActive=true")
    Page<Member> getMkobaMembers(Pageable pageable, String key);

    @Query("SELECT SUM(m.memberShares) FROM Member m WHERE m.isActive=true")
    Double getGroupSavings();

    @Query(value = "SELECT SUM(m.member_shares) FROM members m WHERE m.is_active = true AND EXTRACT(YEAR FROM m.created_at) = :year AND m.id = :memberId", nativeQuery = true)
    Double getTotalMemberSharesByYear(Long memberId,Integer year);

    Optional<Member> findById(Long memberId);

    @Query("SELECT m FROM Member m WHERE m.isActive=true")
    List<Member> findAllAndActive();

    @Query("SELECT m FROM Member m WHERE m.isActive=false and m.removed=true")
    Page<Member> getPastMembers(Pageable pageable, String key);

    @Query("SELECT count(m) FROM Member m WHERE m.isActive=true")
    Integer getTotalActiveMembers();
}
