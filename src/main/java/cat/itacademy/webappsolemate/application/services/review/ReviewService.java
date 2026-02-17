package cat.itacademy.webappsolemate.application.services.review;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.request.UpdateReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long footId, ReviewRequest request);

    List<ReviewResponse> getReviewsByFoot(Long footId);

    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);


}
