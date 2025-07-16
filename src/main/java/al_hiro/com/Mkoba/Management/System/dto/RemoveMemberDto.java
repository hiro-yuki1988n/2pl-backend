package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.enums.RemoveReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveMemberDto {
    private Long memberId;
    private RemoveReason removeReason;
}
