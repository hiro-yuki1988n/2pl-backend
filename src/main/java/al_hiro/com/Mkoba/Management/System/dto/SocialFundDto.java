package al_hiro.com.Mkoba.Management.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialFundDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private Month month;
    private Long memberId;
}
