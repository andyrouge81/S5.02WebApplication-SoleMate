package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.services.foot.FootService;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FootControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FootService footService;

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