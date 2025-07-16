package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.CommonFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommonFundRepository extends JpaRepository<CommonFund, Long> {

    @Query("SELECT c FROM CommonFund c WHERE c.isActive=true")
    Page<CommonFund> findCommonFunds(Pageable pageable, String key);

    @Query("SELECT SUM(c.amount) FROM CommonFund c WHERE c.isActive=true and c.sourceType='social_fund'")
    Double findLeftOverFunds();
}
