package al_hiro.com.Mkoba.Management.System.configuration.dto;

import al_hiro.com.Mkoba.Management.System.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Getter
    public String getToken() {
        return token;
    }
}
