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
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Double amount;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Boolean isPaid;
    private Boolean isPenaltyApplied = false;

    public Double calculatePenalty() {
        if (!isPaid && LocalDate.now().isAfter(dueDate) && !isPenaltyApplied) {
            isPenaltyApplied = true;
            return amount * 0.10; // 10% penalty
        }
        return 0.0;
    }

    public void update() {
        Loan loan = new Loan();
        loan.setMember(this.member);
        loan.setAmount(this.amount);
        loan.setInterestRate(this.interestRate);
        loan.setStartDate(this.startDate);
        loan.setDueDate(this.dueDate);
        loan.setIsPaid(this.isPaid);
        loan.setIsPenaltyApplied(this.isPenaltyApplied);
    }
}
