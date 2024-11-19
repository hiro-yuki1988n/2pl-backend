package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.LoanPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    @Query("select lp from LoanPayment lp lower(concat(lp.id, lp.loan.member.name)) like %:key% AND lp.loan.member.id=:memberId")
    Page<LoanPayment> getLoanPaymentsByMember(Long memberId, Pageable pageable, String key);
}
