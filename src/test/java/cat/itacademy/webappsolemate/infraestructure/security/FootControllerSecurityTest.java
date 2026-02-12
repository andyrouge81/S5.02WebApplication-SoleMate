package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.services.FootService;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FootControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FootService footService;

    @MockBean
    private FootSecurity footSecurity;

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void createFoot_whenUSerExists_theShouldCreateAFoot201Created() throws Exception{

        FootRequest request = new FootRequest(
                "test-foot",
                "http://example.com/img.jpg",
                ArchType.PES_CAVUS

        );

        mockMvc.perform(post("/feet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "owner", roles = "USER")
    void updateFoot_whenOwner_thenReturns200() throws Exception {
        Long footId = 1L;

        FootRequest request = new FootRequest(
                "updated-title",
                "http://example.com/new.jpg",
                ArchType.PES_CAVUS
        );

        FootResponse response = new FootResponse(
                footId,
                "updated-title",
                "http://example.com/new.jpg",
                ArchType.PES_CAVUS,
                "owner",
                LocalDateTime.now()
        );

        when(footSecurity.isOwner(footId)).thenReturn(true);
        when(footService.updateFoot(eq(footId), any(FootRequest.class))).thenReturn(response);

        mockMvc.perform(put("/feet/{footId}", footId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("updated-title"));
    }

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void updateFoot_whenUserIsNotOwner_thenReturns403() throws Exception {
        Long footId = 3L;

        FootRequest request = new FootRequest(
                "forbidden-update",
                "http://example.com/forbidden.jpg",
                ArchType.PES_RECTUS
        );

        when(footSecurity.isOwner(footId)).thenReturn(false);

        mockMvc.perform(put("/feet/{footId}", footId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(footService, never()).updateFoot(any(Long.class), any(FootRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void deleteFoot_givenAnUser_whenTryDeleteOtherUserFoot_canNotForbidden403() throws Exception{

        mockMvc.perform(delete("/feet/{id}", 1L))
        .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteFoot_givenAnAdmin_whenTryDeleteOtherUserFoot_canDelete204() throws Exception {

            doNothing().when(footService).deleteFoot(anyLong());

            mockMvc.perform(delete("/feet/{id}", 1L))
                    .andExpect(status().isNoContent());
    }
}