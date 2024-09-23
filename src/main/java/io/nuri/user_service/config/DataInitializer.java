package io.nuri.user_service.config;

import io.nuri.user_service.entity.Role;
import io.nuri.user_service.entity.UserEntity;
import io.nuri.user_service.respository.RoleRepository;
import io.nuri.user_service.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = null;
        Role userRole = null;
        Optional<Role> optionalAdminRole = roleRepository.findByName("ADMIN");
        if(optionalAdminRole.isEmpty()){
            adminRole = roleRepository.save(Role.builder()
                    .name("ADMIN")
                    .build());
            userRole = roleRepository.save(Role.builder()
                    .name("USER")
                    .build());
        }

        Optional<UserEntity> admin = userRepository.findByEmail("admin");
        if(admin.isEmpty()){
            userRepository.save(UserEntity.builder()
                    .name("admin")
                    .email("admin")
                    .password(encoder.encode("admin"))
                    .roles(Set.of(adminRole))
                    .build());
        }

        Optional<UserEntity> user = userRepository.findByEmail("user");
        if(user.isEmpty()){
            userRepository.save(UserEntity.builder()
                    .name("user")
                    .email("user")
                    .password(encoder.encode("user"))
                    .roles(Set.of(userRole))
                    .build());
        }


    }
}
