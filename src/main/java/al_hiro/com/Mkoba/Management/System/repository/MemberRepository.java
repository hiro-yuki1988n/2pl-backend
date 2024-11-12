package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email=:email")
    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m WHERE lower(concat(m.id, m.name)) like %:key%")
    Page<Member> getMkobaMembers(Pageable pageable, String key);
}
