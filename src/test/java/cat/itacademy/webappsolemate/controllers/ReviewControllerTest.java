package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.review.ReviewService;

import cat.itacademy.webappsolemate.infraestructure.security.JwtAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ReviewService reviewService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    @WithMockUser(roles = "USER")
    void createReview_withValidRequest_returns201() throws Exception {

        ReviewRequest request = new ReviewRequest(
                "Muy buena pisada",
                5
        );

        ReviewResponse response = new ReviewResponse(
                1L,
                "Muy buena pisada",
                5,
                "user@mail.com",
                LocalDateTime.now()
        );

        when(reviewService.createReview(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(post("/feet/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("Muy buena pisada"))
                .andExpect(jsonPath("$.rateAspect").value(5));

    }
}
