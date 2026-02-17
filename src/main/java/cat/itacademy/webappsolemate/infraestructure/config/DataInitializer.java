package cat.itacademy.webappsolemate.infraestructure.config;

import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = User.builder()
                        .username("admin")
                        .email("admin@solemate.com")
                        .password(passwordEncoder.encode("admin1234"))
                        .role(Role.ROLE_ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build();

                userRepository.save(admin);

                System.out.println("User ADMIN created");
            }

        };
    }
}
