package al_hiro.com.Mkoba.Management.System.configuration.service;

import al_hiro.com.Mkoba.Management.System.configuration.UserDetailsServiceImpl;
import al_hiro.com.Mkoba.Management.System.configuration.dto.LoginResponse;
import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import al_hiro.com.Mkoba.Management.System.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(String username, String password) {
        // Authenticate user credentials
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Fetch user from DB
        Optional<User> oUser = userRepository.findByUsername(username);
        if (oUser.isEmpty()) {
            log.info("User not found with email");
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        User user = oUser.get();
        // Generate JWT token
        String token = jwtUtil.generateToken(
                new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>())
        );

        // Return token and user info
        return new LoginResponse(token, user);
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> oUser = userRepository.findByUsername(username);
        if (oUser.isEmpty()) {
            // optional logging
            System.out.println("User not found with email: " + username);
            return false;
        }

        if (!passwordEncoder.matches(currentPassword, oUser.get().getPassword())) {
            // optional logging
            System.out.println("Invalid current password.");
            return false;
        }

        try {
            oUser.get().setPassword(passwordEncoder.encode(newPassword));
            oUser.get().setPasswordChangedAt(LocalDateTime.now());
            userRepository.save(oUser.get());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to change password.");
        }
    }
}


