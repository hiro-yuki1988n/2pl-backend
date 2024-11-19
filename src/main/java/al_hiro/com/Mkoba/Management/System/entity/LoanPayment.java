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
@Table(name = "loan_payments")
public class LoanPayment extends BaseEntity{

    @Column(name = "amount_paid")
    private Double amount;

    @Column(name = "date_of_payment")
    private LocalDate payDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
}
