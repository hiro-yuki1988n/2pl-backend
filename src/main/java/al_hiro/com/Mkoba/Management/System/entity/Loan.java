package al_hiro.com.Mkoba.Management.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Double amount;
    private Double interestRate;
    private Double interestAmount;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Boolean isPaid=false;
    private Boolean isPenaltyApplied = false;
    private Double penaltyAmount;

    public Double calculatePenalty() {
        if (!isPaid && LocalDate.now().isAfter(dueDate) && !isPenaltyApplied) {
            isPenaltyApplied = true;
            return amount * 0.10; // 10% penalty
        }
        return 0.0;
    }
}
