package al_hiro.com.Mkoba.Management.System.configuration.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ChangePasswordResponse {
    private boolean success;
    private String message;

    public ChangePasswordResponse() {}

    public ChangePasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
