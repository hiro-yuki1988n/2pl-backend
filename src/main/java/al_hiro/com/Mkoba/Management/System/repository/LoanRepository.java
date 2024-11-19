package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l WHERE lower(concat(l.id, l.member.name)) like %:key% AND l.isActive=true")
    Page<Loan> getMembersLoans(Pageable pageable, String key);

    @Query("SELECT l FROM Loan l WHERE lower(concat(l.id)) like %:key% AND l.member.id=:memberId AND l.isActive=true")
    Page<Loan> getLoanByMember(Long memberId, Pageable pageable, String key);

    @Query("SELECT SUM(l.interestAmount) FROM Loan l WHERE l.isPaid = true AND l.isActive=true")
    Double getGroupLoansProfit();

    @Query("SELECT SUM(l.penaltyAmount) FROM Loan l WHERE l.isPenaltyApplied = true AND l.isActive=true")
    Double findTotalPenalties();
}
