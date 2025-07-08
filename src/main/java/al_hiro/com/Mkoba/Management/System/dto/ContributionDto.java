package al_hiro.com.Mkoba.Management.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributionDto {
    private Long id;
    private BigDecimal amount;
    private Month month;
//    private LocalDateTime paymentDate;
    private Long memberId;
}

