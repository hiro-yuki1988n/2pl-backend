package al_hiro.com.Mkoba.Management.System.entity;

import al_hiro.com.Mkoba.Management.System.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "expenditures")
public class Expenditures extends BaseEntity{

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date_issued")
    private LocalDateTime dateIssued;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type")
    private ExpenseType expenseType;

    @Column(name = "description")
    private String description;

    @Column(name = "approved", columnDefinition = "boolean DEFAULT false"  )
    private Boolean approved;
}
