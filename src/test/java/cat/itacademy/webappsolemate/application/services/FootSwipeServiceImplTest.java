package cat.itacademy.webappsolemate.application.services;


import cat.itacademy.webappsolemate.application.dto.request.SwipeRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.application.services.swipe.FootSwipeServiceImpl;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.FootSwipe;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootSwipeRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FootSwipeServiceImplTest {

    @Mock FootSwipeRepository footSwipeRepository;
    @Mock FootRepository footRepository;
    @Mock UserRepository userRepository;
    @Mock AuthService authService;

    @InjectMocks
    FootSwipeServiceImpl footSwipeService;

    private User mockUser() {
        return User.builder()
                .id(1L).username("user").email("u@test.com").password("x")
                .role(Role.ROLE_USER).createdAt(LocalDateTime.now())
                .build();
    }

    private Foot mockFoot(User owner) {
        return Foot.builder()
                .id(10L).title("foot").imageUrl("http://img")
                .imageHash("hash").archType(ArchType.PES_CAVUS)
                .owner(owner).createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void saveOrUpdateSwipe_whenNoFootSwipe_thenCreateFootSwipe() {

        CurrentUserResponse current = new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        User user = mockUser();

        Foot foot = mockFoot(user);

        when(authService.getCurrentUser()).thenReturn(current);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.of(foot));
        when(footSwipeRepository.findByUser_IdAndFoot_Id(1L, 10L)).thenReturn(Optional.empty());

        FootSwipe saved = FootSwipe.builder()
                .id(100L).user(user).foot(foot).action(SwipeAction.LIKE)
                .createdAt(LocalDateTime.now())
                .build();
        when(footSwipeRepository.save(any(FootSwipe.class))).thenReturn(saved);

        SwipeResponse result = footSwipeService.saveOrUpdateSwipe(10L, new SwipeRequest(SwipeAction.LIKE));

        assertEquals(10L, result.footId());
        assertEquals(SwipeAction.LIKE, result.action());
        verify(footSwipeRepository).save(any(FootSwipe.class));
    }

    @Test
    void saveOrUpdateSwipe_whenFootSwipeExists_thenUpdateAction() {

        CurrentUserResponse current = new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        User user = mockUser();

        Foot foot = mockFoot(user);

        FootSwipe existing = FootSwipe.builder()
                .id(200L).user(user).foot(foot).action(SwipeAction.LIKE)
                .createdAt(LocalDateTime.now())
                .build();

        when(authService.getCurrentUser()).thenReturn(current);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.of(foot));
        when(footSwipeRepository.findByUser_IdAndFoot_Id(1L, 10L)).thenReturn(Optional.of(existing));
        when(footSwipeRepository.save(existing)).thenReturn(existing);

        SwipeResponse result = footSwipeService.saveOrUpdateSwipe(10L, new SwipeRequest(SwipeAction.DISLIKE));

        assertEquals(SwipeAction.DISLIKE, result.action());
        verify(footSwipeRepository).save(existing);
    }

    @Test
    void saveOrUpdateSwipe_whenUserNotFound_throwsUserNotFoundException() {

        CurrentUserResponse current = new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        when(authService.getCurrentUser()).thenReturn(current);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> footSwipeService.saveOrUpdateSwipe(10L, new SwipeRequest(SwipeAction.LIKE)));

        verify(footRepository, never()).findById(anyLong());
        verify(footSwipeRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateSwipe_whenFootNotFound_throwsFootNotFoundException() {

        CurrentUserResponse current = new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        User user = mockUser();

        when(authService.getCurrentUser()).thenReturn(current);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(FootNotFoundException.class,
                () -> footSwipeService.saveOrUpdateSwipe(10L, new SwipeRequest(SwipeAction.LIKE)));

        verify(footSwipeRepository, never()).save(any());
    }

    @Test
    void getMySwipes_returnsFootList() {

        CurrentUserResponse current = new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        User user = mockUser();

        Foot foot = mockFoot(user);

        List<FootSwipe> swipes = List.of(
                FootSwipe.builder().id(1L).user(user).foot(foot).action(SwipeAction.LIKE).createdAt(LocalDateTime.now()).build(),
                FootSwipe.builder().id(2L).user(user).foot(foot).action(SwipeAction.DISLIKE).createdAt(LocalDateTime.now().minusMinutes(1)).build()
        );

        when(authService.getCurrentUser()).thenReturn(current);
        when(footSwipeRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(swipes);

        List<SwipeResponse> result = footSwipeService.getMySwipes();

        assertEquals(2, result.size());
        assertEquals(SwipeAction.LIKE, result.get(0).action());
        verify(footSwipeRepository).findByUser_IdOrderByCreatedAtDesc(1L);
    }
}
