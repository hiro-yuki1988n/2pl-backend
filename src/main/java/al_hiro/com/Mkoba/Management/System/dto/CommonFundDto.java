package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonFundDto {
    private Long id;
    private BigDecimal amount;
    private SourceType sourceType;
    private LocalDate entryDate;
    private String description;
}
