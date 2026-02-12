package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.services.FootService;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FootController.class)
@AutoConfigureMockMvc(addFilters = false)

class FootControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    FootService footService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllFeet_whenFeetExists_returns200AndList() throws Exception {

        List<FootResponse> feet = List.of(
                new FootResponse(1L, "foot-1","http://img/1",
                        ArchType.PES_CAVUS, "user1", LocalDateTime.now()),
                new FootResponse(2L, "foot-2","http://img/2",
                        ArchType.PES_CAVUS, "user2", LocalDateTime.now())
        );

        when(footService.getAllFeet()).thenReturn(feet);

        mockMvc.perform(get("/feet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("foot-1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createFoot_withValidRequest_returns201() throws Exception {
        FootRequest request = new FootRequest(
                "new-foot",
                "http://img/new",
                ArchType.PES_RECTUS
        );

        FootResponse response = new FootResponse(
                10L,
                "new-foot",
                "http://img/new",
                ArchType.PES_RECTUS,
                "user",
                LocalDateTime.now()
        );

        when(footService.createFoot(any(FootRequest.class))).thenReturn(response);

        mockMvc.perform(post("/feet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("new-foot"))
                .andExpect(jsonPath("$.archType").value("PES_RECTUS"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createFoot_withInvalidRequest_returns400() throws Exception {
        // title en blanco e imageUrl en blanco -> viola @NotBlank
        String invalidJson = """
                {
                  "title": "",
                  "imageUrl": "",
                  "archType": "PES_CAVUS"
                }
                """;

        mockMvc.perform(post("/feet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(footService, never()).createFoot(any(FootRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyFeet_returns200AndList() throws Exception {
        List<FootResponse> myFeet = List.of(
                new FootResponse(1L, "my-foot", "http://img/me", ArchType.PES_CAVUS, "me", LocalDateTime.now())
        );

        when(footService.getMyFeet()).thenReturn(myFeet);

        mockMvc.perform(get("/feet/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ownerUsername").value("me"));
    }



}