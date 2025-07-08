package al_hiro.com.Mkoba.Management.System.controller;

import al_hiro.com.Mkoba.Management.System.dto.MemberDto;
import al_hiro.com.Mkoba.Management.System.dto.UserDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.User;
import al_hiro.com.Mkoba.Management.System.repository.MemberRepository;
import al_hiro.com.Mkoba.Management.System.repository.UserRepository;
import al_hiro.com.Mkoba.Management.System.service.UserService;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@GraphQLApi
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    @GraphQLQuery(name = "getAllUsers", description = "Retrieving all users")
    public ResponsePage<User> getAllUsers(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        return userService.getAllUsers(pageableParam!=null?pageableParam:new PageableParam());
    }

    @GraphQLMutation(name = "createUser", description = "Saving user")
    public Response<User> saveMkobaMember(@GraphQLArgument(name = "userDto") UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GraphQLMutation(name = "deleteUser", description = "Deleting a user by id")
    public Response<User> deleteUser(@GraphQLArgument(name = "id") Long id) {
        return userService.deleteUser(id);
    }
}
