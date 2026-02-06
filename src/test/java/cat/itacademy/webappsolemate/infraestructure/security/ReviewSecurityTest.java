package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.domain.entities.Review;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewSecurityTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewSecurity reviewSecurity;

    private void mockAuthenticateUser(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

    }

    private User mockUser(String username) {
        return User.builder()
                .id(1L)
                .username(username)
                .build();
    }

    private Review mockReview(User owner) {
        return Review.builder()
                .id(10L)
                .reviewer(owner)
                .build();
    }

    @AfterEach
    void cleanCache() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwner_whenUserIsOwner_shouldDeleteOnlyOwnerReviews() {

        mockAuthenticateUser("user");

        User owner = mockUser("user");
        Review review = mockReview(owner);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        boolean result = reviewSecurity.isOwner(1L);

        assertTrue(result);
    }

    @Test
    void isOwner_whenUserIsNotOwner_shouldNotDeleteAnotherUserReviews() {

        mockAuthenticateUser("user");

        User other = mockUser("other");
        Review review = mockReview(other);

        when(reviewRepository.findById(9L)).thenReturn(Optional.of(review));

        boolean result = reviewSecurity.isOwner(9L);

        assertFalse(result);
    }

    @Test
    void isOwner_whenReviewNotExists_returnFalse() {

        mockAuthenticateUser("user");

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = reviewSecurity.isOwner(99L);

        assertFalse(result);

    }

}