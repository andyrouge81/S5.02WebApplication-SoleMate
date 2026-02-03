package cat.itacademy.webappsolemate.application.services.review;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long footId, ReviewRequest request);

    List<ReviewResponse> getReviewsByFoot(Long footId);

    void deleteReview(Long reviewId);


}
