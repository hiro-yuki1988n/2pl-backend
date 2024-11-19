package al_hiro.com.Mkoba.Management.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaymentDto {
    private Long id;
    private Double amount;
    private LocalDate payDate;
    private String description;
    private Long loanId;
}
