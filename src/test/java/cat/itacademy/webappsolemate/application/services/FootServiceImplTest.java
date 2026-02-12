package cat.itacademy.webappsolemate.application.services;

import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.common.exceptions.DuplicateFootException;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.ArchType;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootSwipeRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
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
class FootServiceImplTest {

    @Mock
    private FootRepository footRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FootSwipeRepository footSwipeRepository;

    @InjectMocks
    private FootServiceImpl footService;

    private User mockUser() {
        return User.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();
    }

    private Foot mockFoot(User owner) {
        return Foot.builder()
                .id(10L)
                .title("old-name")
                .imageUrl("http://img.old")
                .archType(ArchType.PES_PLANUS)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createFoot_whenUserExists_thenShouldCreateAFoot() {

        User user = mockUser();
        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        FootRequest request = new FootRequest(
                "new-foot",
                "http://img.new",
                ArchType.PES_CAVUS
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.existsByImageHash(any(String.class))).thenReturn(false);
        when(footRepository.save(any(Foot.class))).thenAnswer(
                inv-> inv.getArgument(0));

        FootResponse response = footService.createFoot(request);

        assertEquals("new-foot", response.title());
        verify(footRepository).save(any(Foot.class));
    }

    @Test
    void createFoot_whenUserNotExists_thenThrowException() {

        CurrentUserResponse currentUser =
                new CurrentUserResponse(55L, "no-exists", Role.ROLE_USER);

        FootRequest request = new FootRequest(
                "new-foot",
                "http:/img.png",
                ArchType.PES_RECTUS
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> footService.createFoot(request));

        verify(footRepository, never()).save(any(Foot.class));

    }

    @Test
    void createFoot_whenImageHashAlreadyExists_throwDuplicateFootException() {
        User user = mockUser();
        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        FootRequest request = new FootRequest(
                "duplicated-foot",
                "http://img.duplicate",
                ArchType.PES_CAVUS
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.existsByImageHash(any(String.class))).thenReturn(true);

        assertThrows(DuplicateFootException.class, () -> footService.createFoot(request));

        verify(footRepository, never()).save(any(Foot.class));
    }


    @Test
    void getAllFeet_whenFeetExists_returnAList() {

        User owner = mockUser();

        List<Foot> feet = List.of(
                mockFoot(owner),
                mockFoot(owner)
        );

        when(footRepository.findAll()).thenReturn(feet);

        List<FootResponse> result = footService.getAllFeet();

        assertEquals(2, result.size());
        verify(footRepository).findAll();

    }

    @Test
    void getMyFeet_whenUserHasFeet_returnOwnerFeetList() {

        User owner = mockUser();

        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user", Role.ROLE_USER);

        List<Foot> feet = List.of(
                mockFoot(owner),
                mockFoot(owner)
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(footRepository.findByOwnerId(1L)).thenReturn(feet);

        List<FootResponse> result = footService.getMyFeet();

        assertEquals(2, result.size());
        verify(footRepository).findByOwnerId(1L);

    }

    @Test
    void updateFoot_whenUserIsOwner_thenUpdateFoot() {

        User owner = mockUser();

        Foot foot = mockFoot(owner);

        FootRequest request = new FootRequest(
                "update-foot",
                "http://image.updated",
                ArchType.PES_PLANUS
        );

        when(footRepository.findById(10L)).thenReturn(Optional.of(foot));
        when(footRepository.findByImageHash(any(String.class))).thenReturn(Optional.empty());
        when(footRepository.save(any(Foot.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        FootResponse response = footService.updateFoot(10L, request);

        assertEquals("update-foot", response.title());
        assertEquals("http://image.updated", response.imageUrl());
        assertEquals(ArchType.PES_PLANUS, request.archType());

        verify(footRepository).save(foot);
    }

    @Test
    void updateFoot_whenUserIsNotUser_theThrowAccessDeniedException() {

        FootRequest request = new FootRequest(
                "update-foot",
                "http://image.updated",
                ArchType.PES_PLANUS
        );

        when(footRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FootNotFoundException.class,
                () -> footService.updateFoot(99L, request));

        verify(footRepository, never()).save(any());

    }

    @Test
    void updateFoot_whenAnotherFootHasSameImageHash_throwDuplicateFootException() {
        User owner = mockUser();

        Foot currentFoot = mockFoot(owner); // id=10L
        Foot duplicatedFoot = Foot.builder()
                .id(20L)
                .title("other-foot")
                .imageUrl("http://img.duplicate")
                .archType(ArchType.PES_PLANUS)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();

        FootRequest request = new FootRequest(
                "updated-title",
                "http://img.duplicate",
                ArchType.PES_CAVUS
        );

        when(footRepository.findById(10L)).thenReturn(Optional.of(currentFoot));
        when(footRepository.findByImageHash(any(String.class))).thenReturn(Optional.of(duplicatedFoot));

        assertThrows(DuplicateFootException.class, () -> footService.updateFoot(10L, request));

        verify(footRepository, never()).save(any(Foot.class));
    }

    @Test
    void deleteFoot_whenFootExists_thenDelete() {

        User owner = mockUser();
        Foot foot = mockFoot(owner);

        when(footRepository.findById(1L)).thenReturn(Optional.of(foot));

        footService.deleteFoot(1L);

        verify(footSwipeRepository).deleteByFoot_Id(1L);
        verify(reviewRepository).deleteByFootId(1L);
        verify(footRepository).delete(foot);

    }

    @Test
    void deleteFoot_whenFootNotExists_thenThrowException() {
        when(footRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FootNotFoundException.class,
                ()-> footService.deleteFoot(1L));

        verify(footSwipeRepository, never()).deleteByFoot_Id(any(Long.class));
        verify(reviewRepository, never()).deleteByFootId(any(Long.class));
        verify(footRepository, never()).delete(any());

    }



}