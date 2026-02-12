package cat.itacademy.webappsolemate.infraestructure.persistence;


import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ifUserExists_returnsUser() {

        User user = User.builder()
                .username("andy")
                .email("andy@mail.com")
                .password("pass")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        var result = userRepository.findByUsername("andy");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("andy@mail.com");
    }

    @Test
    void existsByEmail_ifUserExists_returnsTrue() {

        User user = User.builder()
                .username("admin")
                .email("admin@mail.com")
                .password("pass")
                .role(Role.ROLE_ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("admin@mail.com");

        assertThat(exists).isTrue();
    }

    @Test
    void countByRole_returnsCorrectCount() {

        userRepository.save(User.builder()
                .username("u1").email("u1@mail.com").password("p")
                .role(Role.ROLE_USER).createdAt(LocalDateTime.now()).build());
        userRepository.save(User.builder()
                .username("u2").email("u2@mail.com").password("p")
                .role(Role.ROLE_USER).createdAt(LocalDateTime.now()).build());
        userRepository.save(User.builder()
                .username("a1").email("a1@mail.com").password("p")
                .role(Role.ROLE_ADMIN).createdAt(LocalDateTime.now()).build());

        long users = userRepository.countByRole(Role.ROLE_USER);
        long admins = userRepository.countByRole(Role.ROLE_ADMIN);

        assertThat(users).isEqualTo(2);
        assertThat(admins).isEqualTo(1);
    }
}
