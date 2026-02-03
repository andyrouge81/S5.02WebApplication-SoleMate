package cat.itacademy.webappsolemate.application.services.foot;


import cat.itacademy.webappsolemate.application.dto.request.FootRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.application.mappers.FootMapper;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FootServiceImpl implements FootService {

    private final FootRepository footRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public FootServiceImpl(FootRepository footRepository,
                           UserRepository userRepository,
                           AuthService authService) {
        this.footRepository = footRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public FootResponse createFoot(FootRequest request) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        User owner = userRepository.findById(currentUser.id())
                .orElseThrow( () -> new UserNotFoundException(currentUser.id()));

         Foot foot = Foot.builder()
                 .nickname(request.nickname())
                 .imageUrl(request.imageUrl())
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

        CurrentUserResponse currentUser = authService.getCurrentUser();

        Foot foot = footRepository.findById(footId)
                .orElseThrow(()-> new FootNotFoundException(footId));

        boolean isOwner = foot.getOwner().getId().equals(currentUser.id());

        if(!isOwner) {
            throw new AccessDeniedException("Do are not allowed to update the foot");
        }

        foot.setNickname(request.nickname());
        foot.setImageUrl(request.imageUrl());
        foot.setArchType(request.archType());

        return FootMapper.toResponse(foot);

    }

    @Override
    public void deleteFoot(Long footId) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        Foot foot = footRepository.findById(footId)
                .orElseThrow(() -> new FootNotFoundException(footId));

        boolean isOwner = foot.getOwner().getId().equals(currentUser.id());
        boolean isAdmin = currentUser.role() == Role.ROLE_ADMIN;

        if(!isOwner && !isAdmin) {
            throw new AccessDeniedException("No authorized to delete this foot");
        }

        footRepository.delete(foot);
    }


}
