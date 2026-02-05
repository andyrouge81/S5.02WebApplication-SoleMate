package cat.itacademy.webappsolemate.infraestructure.security;

import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("reviewSecurity")
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    public ReviewSecurity(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public boolean isOwner(Long reviewId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return reviewRepository.findById(reviewId)
                .map(review -> review.getReviewer()
                        .getUsername()
                        .equals(username))
                .orElse(false);
    }
}
