package al_hiro.com.Mkoba.Management.System.repository;

import al_hiro.com.Mkoba.Management.System.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

//    User findByEmail(String email);

//    @Query(value = "SELECT u FROM User u WHERE u.member.email = :email AND u.isActive = true")
//    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE lower(concat(u.member.name, u.member.email)) like %:key% AND u.isActive = true")
    Page<User> findAllUsers(Pageable pageable, String key);

    @Query(value = "SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findByUsername(String username);
}
