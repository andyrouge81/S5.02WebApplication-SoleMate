package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.review.ReviewService;
import cat.itacademy.webappsolemate.controllers.ReviewController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ReviewController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class,
                                SecurityConfig.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity
public class ReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean(name = "reviewSecurity")
    private ReviewSecurity reviewSecurity;


    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user", roles= "USER")
    void createReview_whenUserIsAuthenticated_shouldCreateAReview201() throws Exception {

        ReviewRequest request = new ReviewRequest("Good foot", 3);

        ReviewResponse response = new ReviewResponse(
                1L,
                "Good foot",
                3,
                "user",
                LocalDateTime.now());

        when(reviewService.createReview(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(post("/feet/{footId}/reviews", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithAnonymousUser
    void createReview_whenAnonymous_thenForbidden403() throws Exception {

        ReviewRequest request = new ReviewRequest("Great foot", 5);

        mockMvc.perform(post("/feet/{footId}/reviews", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getReviewsByFoot_whenUserAuthenticated_thenOk200() throws Exception {

        when(reviewService.getReviewsByFoot(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/feet/{footId}/reviews", 1L))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteReview_whenAdmin_shouldDeleteReview() throws Exception {

        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/feet/reviews/{reviewId}", reviewId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "owner", roles = "USER")
    void deleteReview_whenOwner_shouldDeleteReview204() throws Exception {
        Long reviewId = 10L;

        when(reviewSecurity.isOwner(reviewId)).thenReturn(true);
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/feet/reviews/{reviewId}", reviewId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void deleteReview_whenUserIsNotOwner_thenForbidden403() throws Exception {
        Long reviewId = 11L;

        when(reviewSecurity.isOwner(reviewId)).thenReturn(false);

        mockMvc.perform(delete("/feet/reviews/{reviewId}", reviewId))
                .andExpect(status().isForbidden());

        verify(reviewService, never()).deleteReview(anyLong());
    }
}
