package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("reviewSecurity")
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;
    private final AuthService authService;

    public ReviewSecurity(ReviewRepository reviewRepository,
                          AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.authService = authService;
    }

    public boolean isOwner(Long reviewId) {
        try {
            Long currentUserId = authService.getCurrentUser().id();

            return reviewRepository.findById(reviewId)
                    .map(review -> review.getReviewer().getId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
