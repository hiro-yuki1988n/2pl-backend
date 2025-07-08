package al_hiro.com.Mkoba.Management.System.utils;

import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.enums.MemberRole;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Log
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {

        // 1. Create admin Member
        Member member;
        Optional<Member> optionalMember = memberRepository.findById(1L);
        if (optionalMember.isPresent()) {
            log.info("Member already exists");
            member = optionalMember.get();
            member.update();
        } else {
            member = new Member();
        }

        member.setName("Administrator");
        member.setEmail("admin@example.com");
        member.setPhone("0000000000");
        member.setGender("M");
        member.setMemberRole(MemberRole.ADMIN);
        member.setJoiningDate(LocalDate.now());
        member.setIsActive(true);
        member.setIsDeleted(false);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        Optional<User> optionalUser = userRepository.findByUsername("admin@example.com");
        if (optionalUser.isEmpty()) {
            // 2. Create admin User
            User admin = new User();
            admin.setUsername("admin@example.com");
            admin.setMember(savedMember);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setIsAdmin(true);
            admin.setIsActive(true);
            admin.setIsDeleted(false);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("✅ Admin user and member created: admin@example.com / admin123");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }
}
