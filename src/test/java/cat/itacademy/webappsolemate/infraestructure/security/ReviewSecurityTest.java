package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.domain.entities.Review;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewSecurityTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ReviewSecurity reviewSecurity;
    private CurrentUserResponse currentUser(Long id, String username) {
        return new CurrentUserResponse(id, username, Role.ROLE_USER);
    }

    private User user(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    private Review review(Long id, User reviewer) {
        return Review.builder()
                .id(id)
                .reviewer(reviewer)
                .build();
    }

    @AfterEach
    void cleanCache() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwner_whenUserIsOwner_shouldDeleteOnlyOwnerReviews() {
        when(authService.getCurrentUser()).thenReturn(currentUser(1L, "user"));
        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review(1L, user(1L, "user"))));

        boolean result = reviewSecurity.isOwner(1L);

        assertTrue(result);
    }


    @Test
    void isOwner_whenUserIsNotOwner_shouldNotDeleteAnotherUserReviews() {
        when(authService.getCurrentUser()).thenReturn(currentUser(1L, "user"));
        when(reviewRepository.findById(9L))
                .thenReturn(Optional.of(review(9L, user(2L, "other"))));

        boolean result = reviewSecurity.isOwner(9L);

        assertFalse(result);
    }

    @Test
    void isOwner_whenReviewNotExists_returnFalse() {
        when(authService.getCurrentUser()).thenReturn(currentUser(1L, "user"));
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = reviewSecurity.isOwner(99L);

        assertFalse(result);
    }

}