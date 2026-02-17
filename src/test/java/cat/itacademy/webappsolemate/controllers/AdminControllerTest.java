package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.response.AdminListResponse;
import cat.itacademy.webappsolemate.application.services.admin.AdminService;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.security.CustomUserDetailsService;
import cat.itacademy.webappsolemate.infraestructure.security.JwtService;
import cat.itacademy.webappsolemate.infraestructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void listUsers_whenAdmin_thenReturns200() throws Exception {

        AdminListResponse user = new AdminListResponse(
                1L, "user1", "user1@mail.com", Role.ROLE_USER, LocalDateTime.now()
        );

        Page<AdminListResponse> page = new PageImpl<>(
                List.of(user), PageRequest.of(0, 10), 1
        );

        when(adminService.listUsers(eq(null), any())).thenReturn(page);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("user1"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listUsers_whenUser_thenReturns403() throws Exception {

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }
}
