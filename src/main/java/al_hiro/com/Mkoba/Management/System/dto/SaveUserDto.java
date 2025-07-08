package al_hiro.com.Mkoba.Management.System.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaveUserDto {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private Boolean isAdmin;
}
