package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveLoanDto {
    private Long id;
    private Double amount;
    private Long memberId;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Boolean isPaid;
    private Boolean isPenaltyApplied = false;
}
