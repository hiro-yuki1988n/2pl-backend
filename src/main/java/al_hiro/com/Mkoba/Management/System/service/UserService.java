package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.UserDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Response<User> createUser(UserDto userDto) {
        log.info("Creating new user: " + userDto);
        if (userDto == null)
            return Response.error("User object is null");

        if (userDto.getMemberId() == null)
            return new Response<>("Member ID is required");
        if (userDto.getUsername() == null)
            return new Response<>("Username not provided");
        if (userDto.getPassword() == null)
            return new Response<>("Password is required");

        // Check if username already exists and belongs to a different user
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent() &&
                (userDto.getId() == null || !existingUser.get().getId().equals(userDto.getId()))) {
            return new Response<>("Username already exists");
        }

        User user = (userDto.getId() != null)
                ? userRepository.findById(userDto.getId()).orElse(new User())
                : new User();

        user.setMember(Utils.entity(Member.class, userDto.getMemberId()));
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()) );
        user.setIsAdmin(userDto.getIsAdmin());

        try {
            user = userRepository.save(user);
            return new Response<>(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("Failed to create user");
        }
    }

    public ResponsePage<User> getAllUsers(PageableParam pageableParam) {
        log.info("Getting all users with page");
        return new ResponsePage<>(userRepository.findAllUsers(pageableParam.getPageable(true), pageableParam.key()));
    }

    public Response deleteUser(Long id) {
        log.info("Deleting user with id: " + id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            return new Response<>("User not found");
        User user = optionalUser.get();
        user.delete();
        try {
            user = userRepository.save(user);
            return new Response<>(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("Failed to delete user");
        }
    }
}
