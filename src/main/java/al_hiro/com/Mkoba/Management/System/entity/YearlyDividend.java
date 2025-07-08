package al_hiro.com.Mkoba.Management.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "yearly_dividend")
public class YearlyDividend extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "year")
    private int year;

    @Column(name = "allocated_amount", precision = 12, scale = 2)
    private BigDecimal allocatedAmount; // mgao wa mwaka huu kwa member

    @Column(name = "withdrawn_amount", precision = 12, scale = 2)
    private BigDecimal withdrawnAmount = BigDecimal.ZERO; // kiasi alichochukua

    @Column(name = "remaining_balance", precision = 12, scale = 2)
    private BigDecimal remainingBalance; // salio bado hajachukua

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
