package al_hiro.com.Mkoba.Management.System.entity;

import al_hiro.com.Mkoba.Management.System.enums.SourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "common_funds")
public class CommonFund extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "amount"  , precision = 11, scale = 2)
    private BigDecimal amount;

    @Column(name = "fund_type")
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(name = "date_paid")
    private LocalDate entryDate;

    @Column(name = "description")
    private String description;
}
