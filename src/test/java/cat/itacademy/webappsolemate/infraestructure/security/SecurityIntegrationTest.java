package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUsers() {
        createUserIfNotExists("user@mail.com", Role.ROLE_USER);
        createUserIfNotExists("admin@mail.com", Role.ROLE_ADMIN);
    }

    private void createUserIfNotExists(String username, Role role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(
                    cat.itacademy.webappsolemate.domain.entities.User.builder()
                            .username(username)
                            .email(username)
                            .password(passwordEncoder.encode("password"))
                            .role(role)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
    }



    @Test
    void authMe_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authMe_withUserToken_returns200() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
                .andExpect(status().isOk());
    }

    @Test
    void adminHealth_withUserToken_returns403() throws Exception {
        mockMvc.perform(get("/auth/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminHealth_withAdminToken_returns200() throws Exception {
        mockMvc.perform(get("/auth/admin/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
                .andExpect(status().isOk());
    }



    @Test
    void getFeet_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/feet"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createFoot_withoutToken_returns403() throws Exception {
        mockMvc.perform(post("/feet"))
                .andExpect(status().isForbidden());
    }



    @Test
    void createReview_withoutToken_returns403() throws Exception {
        mockMvc.perform(post("/feet/1/reviews"))
                .andExpect(status().isForbidden());
    }



    private String userToken() {
        return jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername("user@mail.com")
                        .password("password")
                        .roles("USER")
                        .build()
        );
    }

    private String adminToken() {
        return jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername("admin@mail.com")
                        .password("password")
                        .roles("ADMIN")
                        .build()
        );
    }
}

