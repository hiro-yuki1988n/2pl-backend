package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private LocalDate joiningDate;
    private MemberRole memberRole;
}
