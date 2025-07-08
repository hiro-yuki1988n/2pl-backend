package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpendituresDto {

    private Long id;

    private Double amount;

    private LocalDateTime dateIssued;

    private ExpenseType expenseType;

    private String description;
}
