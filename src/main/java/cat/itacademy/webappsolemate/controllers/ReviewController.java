package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.review.ReviewService;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review endpoints")
@RestController
@RequestMapping("/feet")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{footId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            @PathVariable Long footId, @Valid @RequestBody ReviewRequest request) {

        return reviewService.createReview(footId, request);
    }

    @GetMapping("/{footId}/reviews")
    List<ReviewResponse> getReviewsByFoot(@PathVariable Long footId) {

        return reviewService.getReviewsByFoot(footId);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteReview(@PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);
    }



}
