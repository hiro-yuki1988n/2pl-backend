package al_hiro.com.Mkoba.Management.System.dto;

import al_hiro.com.Mkoba.Management.System.enums.MemberRole;
import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private MemberRole memberRole;
}
