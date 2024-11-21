package al_hiro.com.Mkoba.Management.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "social_funds")
public class SocialFund extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "amount"  , precision = 11, scale = 2)
    private BigDecimal amount;

    @Column(name = "date_paid")
    private LocalDateTime paymentDate;

    @Column(name = "month")
    @Enumerated(EnumType.STRING)
    private Month month; // Month for which the social fund is being made (format: YYYY-MM)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
}
