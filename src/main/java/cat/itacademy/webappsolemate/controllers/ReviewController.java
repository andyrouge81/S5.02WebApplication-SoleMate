package cat.itacademy.webappsolemate.controllers;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.request.UpdateReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.services.review.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{footId}/reviews")

    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long footId, @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(footId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{footId}/reviews")
    List<ReviewResponse> getReviewsByFoot(@PathVariable Long footId) {

        return reviewService.getReviewsByFoot(footId);
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @reviewSecurity.isOwner(#reviewId)")
    public ReviewResponse updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
            ) {
        return reviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @reviewSecurity.isOwner(#reviewId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteReview(@PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);
    }



}
