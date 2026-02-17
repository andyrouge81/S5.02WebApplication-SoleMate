package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.review.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewSecurity reviewSecurity;

    @MockBean
    private ReviewService reviewService;

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void createReview_givenAnAuthorizeUser_whenCreateAReview_returnCreated201() throws Exception {

        Long footId = 1L;

        ReviewRequest request = new ReviewRequest(
                "Great foot",
                4
        );

        ReviewResponse response = new ReviewResponse(1L,"Great foot",
                4, "Manuel", LocalDateTime.now());


        when(reviewService.createReview(eq(1L), any(ReviewRequest.class))).thenReturn(response);

        mockMvc.perform(post("/feet/{footId}/reviews", footId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    void createReview_whenAnonymous_thenForbidden403() throws Exception {
        Long footId = 1L;

        ReviewRequest request = new ReviewRequest(
                "Great foot",
                4
        );

        mockMvc.perform(post("/feet/{footId}/reviews", footId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void deleteReview_givenAnAuthorized_whenTryDeleteOtherUserReview_throwException() throws Exception{
        Long reviewId = 55L;

        when(reviewSecurity.isOwner(reviewId)).thenReturn(false);

        mockMvc.perform(delete("/feet/reviews/{reviewId}", reviewId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteReview_givenAnAdminRole_shouldDeleteAnyReview() throws Exception {

        Long reviewId = 99L;

        mockMvc.perform(delete("/feet/reviews/{reviewId}", reviewId))
                .andExpect(status().isNoContent());


    }



}