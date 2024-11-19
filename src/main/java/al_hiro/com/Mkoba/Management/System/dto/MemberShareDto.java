package al_hiro.com.Mkoba.Management.System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberShareDto {

    private Long id;

    private Long memberId;

    private Double amount;

    private String description;
}
