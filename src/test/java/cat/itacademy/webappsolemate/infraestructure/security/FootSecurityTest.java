package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.services.foot.FootService;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FootSecurityTest {

    @Mock
    private FootRepository footRepository;

    @InjectMocks
    private FootSecurity footSecurity;

    private void mockAuthenticatedUser(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearCache() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwner_whenUserIsOwner_returnTrue() {
         mockAuthenticatedUser("user");

         User owner = User.builder()
                 .id(1L)
                 .username("user")
                 .build();

         Foot foot = Foot.builder()
                 .id(2L)
                 .owner(owner)
                 .build();

         when(footRepository.findById(2L))
                 .thenReturn(Optional.of(foot));

         boolean result = footSecurity.isOwner(2L);

         assertTrue(result);
    }

    @Test
    void isOwner_whenAnUserExists_thenUserCanNotEditOtherUserFeet() {

        mockAuthenticatedUser("user");

        User otherUser = User.builder()
                .id(1L)
                .username("other")
                .build();

        Foot foot = Foot.builder()
                .id(10L)
                .owner(otherUser)
                .build();

        when(footRepository.findById(10L))
                .thenReturn(Optional.of(foot));

        boolean result = footSecurity.isOwner(10L);

        assertFalse(result);

    }

    @Test
    void isOwner_whenFootNotExists_returnFalse() {

        mockAuthenticatedUser("user");

        when(footRepository.findById(55L)).thenReturn(Optional.empty());

        boolean result = footSecurity.isOwner(55L);

        assertFalse(result);
    }
}
