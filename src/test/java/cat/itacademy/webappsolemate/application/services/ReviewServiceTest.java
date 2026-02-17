package cat.itacademy.webappsolemate.application.services;


import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.application.services.review.ReviewServiceImpl;
import cat.itacademy.webappsolemate.common.exceptions.DuplicateReviewException;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.ReviewNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.Review;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FootRepository footRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    ReviewServiceImpl reviewService;

    private User user;
    private User admin;
    private Foot foot;
    private Review review;
    private ReviewRequest request;

    @BeforeEach
    void setup () {
        user = User.builder()
                .id(1L)
                .build();
        admin = User.builder()
                .id(2L)
                .build();

        foot = Foot.builder()
                .id(10L)
                .build();

        review = Review.builder()
                .id(110L)
                .reviewer(user)
                .foot(foot)
                .build();

        request = new ReviewRequest("Nice foot" , 4);
    }

    @Test
    void createReview_givenAnExistReview_shouldBeOk() {

        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.of(foot));
        when(reviewRepository.existsByReviewer_IdAndFoot_Id(1L, 10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        ReviewResponse response = reviewService.createReview(10L, request);

        assertNotNull(response);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_whenCreateAReview_throwUserNotFound() {

        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> reviewService.createReview(10L, request));
    }

    @Test
    void createReview_whenCreateAReview_throwFootNotFound() {

        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(FootNotFoundException.class,
                () -> reviewService.createReview(10L, request));
    }

    @Test
    void createReview_whenCreateTheSameReview_throwDuplicateReview() {

        CurrentUserResponse currentUser =
                new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(footRepository.findById(10L)).thenReturn(Optional.of(foot));
        when(reviewRepository.existsByReviewer_IdAndFoot_Id(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateReviewException.class,
                () -> reviewService.createReview(10L, request));
    }

    @Test
    void getReviewsByFoot_throwsFootNotFound() {

        when(footRepository.existsById(10L)).thenReturn(false);

        assertThrows(FootNotFoundException.class,
                () -> reviewService.getReviewsByFoot(10L));
    }

    @Test
    void getReviewsByFoot_whenNoReviews_returnEmptyList() {

        when(footRepository.existsById(10L)).thenReturn(true);
        when(reviewRepository.findByFootId(10L)).thenReturn(List.of());

        List<ReviewResponse> reviews = reviewService.getReviewsByFoot(10L);

        assertTrue(reviews.isEmpty());
    }

    @Test
    void getReviewsByFoot_whenFootExists_shouldCreateReview() {

        when(footRepository.existsById(10L)).thenReturn(true);
        when(reviewRepository.findByFootId(10L)).thenReturn(List.of(review));

        List<ReviewResponse> reviews = reviewService.getReviewsByFoot(10L);

        assertEquals(1, reviews.size());
    }

    @Test
    void deleteReview_whenReviewNotExists_throwReviewNotFound() {

        when(authService.getCurrentUser())
                .thenReturn(new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER));

        when(reviewRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.deleteReview(100L));
    }

    @Test
    void deleteReview_whenOwnerExists_thenDeleteOwnerReview() {

        when(authService.getCurrentUser())
                .thenReturn(new CurrentUserResponse(1L, "user@mail.com", Role.ROLE_USER));

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(100L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_whenAdminIsAuthorized_thenReviewDeleteOk() {

        when(authService.getCurrentUser())
                .thenReturn(new CurrentUserResponse(2L, "admin@mail.com", Role.ROLE_ADMIN));

        review.setReviewer(user);

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(100L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_whenUserNotAuthorized_throwAccessDenied() {

        when(authService.getCurrentUser())
                .thenReturn(new CurrentUserResponse(3L, "other@mail.com", Role.ROLE_USER));

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        assertThrows(AccessDeniedException.class,
                () -> reviewService.deleteReview(100L));
    }



}
