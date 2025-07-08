package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.LoanPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    @Query("select lp from LoanPayment lp where lower(concat(lp.loan.member.name, lp.id)) like %:key% AND lp.loan.member.id=:memberId AND EXTRACT(YEAR FROM lp.payDate) = :year AND lp.isActive=true")
    Page<LoanPayment> getLoanPaymentsByMember(Long memberId, Pageable pageable, String key, Integer year);

    @Query("select lp from LoanPayment lp where lower(concat(lp.loan.member.name, lp.id)) like %:key% AND EXTRACT(YEAR FROM lp.payDate) = :year AND lp.isActive=true")
    Page<LoanPayment> getLoanPayments(Pageable pageable, String key, Integer year);
}
