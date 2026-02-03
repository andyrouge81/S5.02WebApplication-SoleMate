package cat.itacademy.webappsolemate.application.mappers;

import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.domain.entities.Review;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewMapper {

    public static ReviewResponse toResponse(Review review) {

        return new ReviewResponse(
                review.getId(),
                review.getComment(),
                review.getRateAspect(),
                review.getReviewer().getUsername(),
                review.getCreatedAt()
        );
    }

}
