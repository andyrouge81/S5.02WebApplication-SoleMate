package cat.itacademy.webappsolemate.controllers;


import cat.itacademy.webappsolemate.application.dto.request.SwipeRequest;
import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.application.services.swipe.FootSwipeService;
import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FootSwipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class FootSwipeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean FootSwipeService footSwipeService;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "USER")
    void saveOrUpdateSwipe_whenValidRequest_returns201() throws Exception {

        SwipeRequest request = new SwipeRequest(SwipeAction.LIKE);

        SwipeResponse response = new SwipeResponse(10L, SwipeAction.LIKE, LocalDateTime.now());

        when(footSwipeService.saveOrUpdateSwipe(eq(10L), any(SwipeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/feet/10/swipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.footId").value(10))
                .andExpect(jsonPath("$.action").value("LIKE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void saveOrUpdateSwipe_whenInvalidRequest_returns400() throws Exception {

        String invalidJson = """
                { "action": null }
                """;

        mockMvc.perform(post("/feet/10/swipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(footSwipeService, never()).saveOrUpdateSwipe(any(Long.class), any(SwipeRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMySwipes_returns200AndList() throws Exception {

        List<SwipeResponse> swipes = List.of(
                new SwipeResponse(10L, SwipeAction.LIKE, LocalDateTime.now()),
                new SwipeResponse(11L, SwipeAction.DISLIKE, LocalDateTime.now().minusMinutes(1))
        );

        when(footSwipeService.getMySwipes()).thenReturn(swipes);

        mockMvc.perform(get("/feet/swipes/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].action").value("LIKE"));
    }
}

