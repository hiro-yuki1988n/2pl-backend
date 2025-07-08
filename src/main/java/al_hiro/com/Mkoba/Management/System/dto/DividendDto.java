package al_hiro.com.Mkoba.Management.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DividendDto {
    private Long id;
    private Long memberId;
    private BigDecimal withdrawnAmount;
}
