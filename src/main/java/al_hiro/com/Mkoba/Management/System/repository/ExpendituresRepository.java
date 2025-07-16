package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.Expenditures;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExpendituresRepository extends JpaRepository<Expenditures,Long> {

    @Query("select xp from Expenditures xp where lower(concat(xp.id, xp.description)) like %:key% AND EXTRACT(YEAR FROM xp.createdAt) = :year AND xp.isActive=true")
    Page<Expenditures> getExpenditures(Pageable pageable, String key, Integer year);

    @Query("SELECT SUM(xp.amount) FROM Expenditures xp WHERE xp.isActive=true and xp.approved=true ")
    Double calculateTotalExpenditures();

    @Query("SELECT SUM(xp.amount) FROM Expenditures xp WHERE xp.isActive=true and xp.approved=true and xp.expenseType='SOCIAL_EXPENSE'")
    Double getSocialExpenses();
}
