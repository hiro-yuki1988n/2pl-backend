package al_hiro.com.Mkoba.Management.System.configuration;

import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> oUser = userRepository.findByUsername(username);
        if(oUser.isEmpty())
            log.warning("User not found with email: " + username);

        return new org.springframework.security.core.userdetails.User(
                oUser.get().getUsername(), oUser.get().getPassword(), new ArrayList<>());
    }
}
