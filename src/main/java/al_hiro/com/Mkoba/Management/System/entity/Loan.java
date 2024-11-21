package al_hiro.com.Mkoba.Management.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loans")
public class Loan extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "amount")
    private Double amount;
    
    @Column(name = "interest_rate")
    private Double interestRate;
    
    @Column(name = "interest")
    private Double interestAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_paid")
    private Boolean isPaid=false;

    @Column(name = "is_penalty_applied")
    private Boolean isPenaltyApplied = false;

    @Column(name = "penalty")
    private Double penaltyAmount;

    public double calculatePenalty() {
        if (!isPaid && LocalDate.now().isAfter(dueDate) && !isPenaltyApplied) {
            isPenaltyApplied = true;
            return amount * 0.10; // 10% penalty
        }
        return 0.0;
    }
}
