package cat.itacademy.webappsolemate.application.services.review;

import cat.itacademy.webappsolemate.application.dto.request.ReviewRequest;
import cat.itacademy.webappsolemate.application.dto.request.UpdateReviewRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.ReviewResponse;
import cat.itacademy.webappsolemate.application.mappers.ReviewMapper;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
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

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final FootRepository footRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             AuthService authService,
                             FootRepository footRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.authService = authService;
        this.footRepository = footRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponse createReview(Long footId, ReviewRequest request) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        User reviewer = userRepository.findById(currentUser.id())
                .orElseThrow(()-> new UserNotFoundException(currentUser.id()));

        Foot foot = footRepository.findById(footId)
                .orElseThrow(()-> new FootNotFoundException(footId));

        boolean alreadyReviewed = reviewRepository.existsByReviewer_IdAndFoot_Id(reviewer.getId(), footId);

        if(alreadyReviewed) {
            throw new DuplicateReviewException();
        }

        Review review = Review.builder()
                .comment(request.comment())
                .rateAspect(request.rateAspect())
                .reviewer(reviewer)
                .foot(foot)
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewMapper.toResponse(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByFoot(Long footId) {

        if(!footRepository.existsById(footId)) {
            throw new FootNotFoundException(footId);
        }

        return reviewRepository.findByFootId(footId).stream()
                .map(ReviewMapper::toResponse)
                .toList();

    }

    @Override
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ReviewNotFoundException(reviewId));

        boolean isOwner = review.getReviewer().getId().equals(currentUser.id());
        boolean isAdmin = currentUser.role() == Role.ROLE_ADMIN;

        if(!isOwner && !isAdmin) {
            throw new AccessDeniedException("No authorized to update this review");
        }

        review.setComment(request.comment().trim());
        review.setRateAspect(request.rateAspect());

        Review saved = reviewRepository.save(review);
        return ReviewMapper.toResponse(saved);
    }


    @Override
    public void deleteReview(Long reviewId) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ReviewNotFoundException(reviewId));

        boolean isOwner = review.getReviewer().getId().equals(currentUser.id());
        boolean isAdmin = currentUser.role() == Role.ROLE_ADMIN;

        if(!isOwner && !isAdmin) {
            throw new AccessDeniedException("No authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

}
