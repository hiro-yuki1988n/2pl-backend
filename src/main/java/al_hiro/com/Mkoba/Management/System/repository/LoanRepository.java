package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l WHERE lower(concat(l.id, l.member.name)) like %:key% AND l.isActive=true")
    Page<Loan> getMembersLoans(Pageable pageable, String key);

    @Query("SELECT l FROM Loan l WHERE lower(CAST(l.id AS string)) like %:key% AND l.member.id=:memberId AND EXTRACT(YEAR FROM l.startDate) = :year AND l.isActive=true")
    Page<Loan> getLoanByMember(Long memberId, Pageable pageable, String key, Integer year);

    @Query("SELECT SUM(l.interestAmount) FROM Loan l WHERE l.isActive=true")
    Double findTotalLoansProfit();

    @Query("SELECT SUM(l.penaltyAmount) FROM Loan l WHERE l.isPenaltyApplied = true AND l.isActive=true AND MONTH(l.startDate) = :month AND EXTRACT(YEAR FROM l.startDate) = :year")
    Double findLoanPenalties(int month, int year);

    @Query("SELECT SUM(l.interestAmount) FROM Loan l WHERE MONTH(l.startDate) = :month AND l.isActive=true and EXTRACT(YEAR FROM l.startDate) = :year")
    Double findLoansProfitByMonth(Integer month, int year);

    @Query("SELECT SUM(l.amount) FROM Loan l WHERE MONTH(l.startDate) = :month AND l.isActive=true")
    Double getSumOfLoansForMonth(Month month);

    @Query("SELECT SUM(l.penaltyAmount) FROM Loan l WHERE MONTH(l.startDate) = :month AND l.isActive=true")
    Double findCurrentMonthLoanPenalties(Month month);

    @Query("SELECT SUM(l.payableAmount) FROM Loan l WHERE YEAR(l.startDate) = :year AND l.isActive=true")
    Double findTotalDebt(Integer year);
//    SELECT SUM(c.penalty_amount) FROM contributions c WHERE c.is_active = true AND EXTRACT(MONTH FROM c.date_paid) = :monthValue and EXTRACT(YEAR FROM c.date_paid) = :year
//    SUM(c.penaltyAmount) FROM Contribution c WHERE c.penaltyApplied = true AND c.isActive=true and c.month = :month AND c.year=:year
    @Query(value = """
    SELECT
        COALESCE((SELECT SUM(l.penalty) FROM loans l WHERE l.is_active = true AND EXTRACT(MONTH FROM l.start_date) = :monthValue and EXTRACT(YEAR FROM l.created_at) = :year), 0) +
        COALESCE((SELECT SUM(c.penalty_amount) FROM contributions c WHERE c.penalty_applied = true AND c.is_active=true and c.month = :month AND c.year=:year), 0)
    """, nativeQuery = true)
    Double findTotalPenaltiesByMonth(Integer monthValue, String month, int year);

    @Query("SELECT SUM(l.payableAmount) FROM Loan l WHERE l.isActive=true AND l.member.id=:memberId AND EXTRACT(YEAR FROM l.createdAt) = :year")
    Double getTotalLoansByMember(Long memberId, Integer year);

    @Query(value = """
    SELECT
        COALESCE((SELECT SUM(l.penalty) FROM loans l WHERE l.is_active = true AND EXTRACT(YEAR FROM l.start_date) = :year AND l.member_id=:memberId), 0) +
        COALESCE((SELECT SUM(c.penalty_amount) FROM contributions c WHERE c.is_active = true AND EXTRACT(YEAR FROM c.date_paid) = :year AND c.member_id=:memberId), 0)
    """, nativeQuery = true)
    Double findTotalPenaltiesByMember(Long memberId, Integer year);

    @Query("SELECT l FROM Loan l WHERE lower(CAST(l.id AS string)) like %:key% AND l.member.id=:memberId AND EXTRACT(YEAR FROM l.startDate) = :year AND" +
            " l.isPenaltyApplied=true AND l.isActive=true")
    Page<Loan> findLatePaidLoansByMember(Long memberId, Pageable pageable, String key, Integer year);

    List<Loan> findAllByIsPaidFalseAndIsPenaltyAppliedFalseAndDueDateBefore(LocalDate now);
}