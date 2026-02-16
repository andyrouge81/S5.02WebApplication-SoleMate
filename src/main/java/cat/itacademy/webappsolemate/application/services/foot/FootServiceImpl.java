package cat.itacademy.webappsolemate.application.services.foot;


import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.mappers.FootMapper;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.common.exceptions.DuplicateFootException;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.infraestructure.config.HashUtils;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootSwipeRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FootServiceImpl implements FootService {

    private final FootRepository footRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ReviewRepository reviewRepository;
    private final FootSwipeRepository footSwipeRepository;

    public FootServiceImpl(FootRepository footRepository,
                           UserRepository userRepository,
                           AuthService authService, ReviewRepository reviewRepository,
                           FootSwipeRepository footSwipeRepository) {
        this.footRepository = footRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.reviewRepository = reviewRepository;
        this.footSwipeRepository = footSwipeRepository;
    }

    @Override
    public FootResponse createFoot(FootRequest request) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        User owner = userRepository.findById(currentUser.id())
                .orElseThrow( () -> new UserNotFoundException(currentUser.id()));

        String normalizedTitle = request.title() == null ? "" : request.title().trim();
        String normalizedImageUrl = request.imageUrl() == null ? "" : request.imageUrl().trim();
        String imageHash = HashUtils.sha256Hex(normalizedImageUrl);

        if(footRepository.existsByImageHash(imageHash)) {
            throw new DuplicateFootException();
        }

         Foot foot = Foot.builder()
                 .title(normalizedTitle)
                 .imageUrl(normalizedImageUrl)
                 .imageHash(imageHash)
                 .archType(request.archType())
                 .owner(owner)
                 .createdAt(LocalDateTime.now())
                 .build();

         Foot saved = footRepository.save(foot);

         return FootMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FootResponse> getAllFeet() {
        return footRepository.findAll().stream()
                .map(FootMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FootResponse> getMyFeet() {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        return footRepository.findByOwnerId(currentUser.id()).stream()
                .map(FootMapper::toResponse)
                .toList();
    }

    @Override
    public FootResponse updateFoot(Long footId, FootRequest request) {

        Foot foot = footRepository.findById(footId)
                .orElseThrow(()-> new FootNotFoundException(footId));

        String normalizedTitle = request.title() == null ? "" : request.title().trim();
        String normalizedImageUrl = request.imageUrl() == null ? "" : request.imageUrl().trim();
        String newImageHash = HashUtils.sha256Hex(normalizedImageUrl);

        Optional<Foot> existingByHash = footRepository.findByImageHash(newImageHash);
        if(existingByHash.isPresent() && !existingByHash.get().getId().equals(footId)) {
            throw new DuplicateFootException(existingByHash.get().getId());
        }

        foot.setTitle(request.title());
        foot.setImageUrl(request.imageUrl());
        foot.setImageHash(newImageHash);
        foot.setArchType(request.archType());

        return FootMapper.toResponse(footRepository.save(foot));
    }

    @Override
    public void deleteFoot(Long footId) {

        Foot foot = footRepository.findById(footId)
                .orElseThrow(() -> new FootNotFoundException(footId));

        footSwipeRepository.deleteByFoot_Id(footId);
        reviewRepository.deleteByFootId(footId);
        footRepository.delete(foot);
    }

}
