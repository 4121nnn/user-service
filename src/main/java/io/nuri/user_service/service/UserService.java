package io.nuri.user_service.service;

import io.jsonwebtoken.Claims;
import io.nuri.user_service.dto.NewUserRequest;
import io.nuri.user_service.dto.UserDto;
import io.nuri.user_service.entity.Role;
import io.nuri.user_service.entity.UserEntity;
import io.nuri.user_service.respository.RoleRepository;
import io.nuri.user_service.respository.UserRepository;
import io.nuri.user_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDto createUser(NewUserRequest userRequest) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userRequest.email());
        if (optionalUser.isPresent()) {
            System.out.println("Email exist");
            return null;
        }
        UserEntity newUser = userRepository.save(UserEntity.builder()
                .name(userRequest.email())
                .email(userRequest.email())
                .password(encoder.encode(userRequest.password()))
                .build());

        return mapper(newUser);
    }

    public UserDto getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email does not exist"));

        return mapper(user);
    }

    public String oauth2Handler(String name, String email){
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();
            List<String> roles = new ArrayList<>();
            for(Role role : userEntity.getRoles()){
                roles.add(role.getName());
            }
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(userEntity.getEmail())
                    .password("")
                    .roles(roles.toArray(new String[0]))
                    .build();
            return JwtService.generateToken(userDetails);
        }else{
            Role role = roleRepository.findByName("USER").orElse(new Role(1, "USER"));

            UserEntity newUser = userRepository.save(UserEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .name(name)
                    .email(email)
                    .password(encoder.encode("RANDOM_PASSWORD"))
                    .roles(Set.of(role))
                    .build());

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(newUser.getEmail())
                    .password("")
                    .roles("USER")
                    .build();

            return JwtService.generateToken(userDetails);
        }
    }

    private UserDto mapper(UserEntity user) {
        return new UserDto(user.getName(), user.getEmail());
    }

    public String exchangeOneTimeToken(String oneTimeToken) {
        Claims claims = JwtService.getClaimsOfOneTimeToken(oneTimeToken);

        String name = (String) claims.get("name");
        String email = claims.getSubject();
        return oauth2Handler(name, email);
    }
}
