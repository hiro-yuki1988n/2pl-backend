package al_hiro.com.Mkoba.Management.System.configuration.controller;

import al_hiro.com.Mkoba.Management.System.configuration.service.AuthService;
import al_hiro.com.Mkoba.Management.System.configuration.dto.ChangePasswordRequest;
import al_hiro.com.Mkoba.Management.System.configuration.dto.ChangePasswordResponse;
import al_hiro.com.Mkoba.Management.System.configuration.dto.LoginRequest;
import al_hiro.com.Mkoba.Management.System.configuration.dto.LoginResponse;
import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException ex) {
            System.out.println("Invalid credentials for: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ChangePasswordResponse(false, "User not authenticated"));
        }

        String username = authentication.getName();
        Optional<User> oUser = userRepository.findByUsername(username);

        if (oUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ChangePasswordResponse(false, "User not found"));
        }
        User user = oUser.get();
        boolean result = authService.changePassword(user.getUsername(), request.getCurrentPassword(), request.getNewPassword());

        if (result) {
            return ResponseEntity.ok(new ChangePasswordResponse(true, "Password changed successfully."));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ChangePasswordResponse(false, "Current password is incorrect."));
        }
    }
}