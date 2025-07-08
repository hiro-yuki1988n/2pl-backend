package al_hiro.com.Mkoba.Management.System.configuration.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
