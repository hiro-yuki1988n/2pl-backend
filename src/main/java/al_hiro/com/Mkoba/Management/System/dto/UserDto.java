package al_hiro.com.Mkoba.Management.System.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private Long memberId;
    private String username;
    private String password;
    private Boolean isAdmin;
}
