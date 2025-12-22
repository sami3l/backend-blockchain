package com.clinchain.backend.config;

import com.clinchain.backend.model.User;
import com.clinchain.backend.model.UserRole;
import com.clinchain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Seeding default users...");

            createUser("grossiste", "password", UserRole.GROSSISTE);
            createUser("hopitale", "password", UserRole.HOPITALE);
            createUser("pharmacien", "password", UserRole.PHARMACIEN);
            createUser("infirmier", "password", UserRole.INFIRMIER);

            log.info("✅ Default users created successfully:");
            log.info("   - grossiste/password (GROSSISTE)");
            log.info("   - hopitale/password (HOPITALE)");
            log.info("   - pharmacien/password (PHARMACIEN)");
            log.info("   - infirmier/password (INFIRMIER)");
        } else {
            log.info("Users already exist, skipping seed data");
        }
    }

    private void createUser(String username, String password, UserRole role) {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        userRepository.save(user);
    }
}
