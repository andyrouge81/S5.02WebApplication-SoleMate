package cat.itacademy.webappsolemate.infraestructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewSecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createReview_withoutToken_returns403() throws Exception {
        mockMvc.perform(post("/feet/1/reviews"))
                .andExpect(status().isForbidden());
    }
}
