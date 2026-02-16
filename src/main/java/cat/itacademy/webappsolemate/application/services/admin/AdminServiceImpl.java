package cat.itacademy.webappsolemate.application.services.admin;

import cat.itacademy.webappsolemate.application.dto.request.UpdateAdminRequest;
import cat.itacademy.webappsolemate.application.dto.response.AdminListResponse;
import cat.itacademy.webappsolemate.application.mappers.AdminMapper;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.domain.enums.Role;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootSwipeRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.ReviewRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final FootRepository footRepository;
    private final ReviewRepository reviewRepository;
    private final FootSwipeRepository footSwipeRepository;

    public AdminServiceImpl(UserRepository userRepository, AuthService authService,
                            FootRepository footRepository, ReviewRepository reviewRepository,
                            FootSwipeRepository footSwipeRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.footRepository = footRepository;
        this.reviewRepository = reviewRepository;
        this.footSwipeRepository = footSwipeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminListResponse> listUsers(String search, Pageable pageable) {

        Page<User> page;

        if(search == null || search.isBlank()) {
            page = userRepository.findAll(pageable);
        } else {
            String q = search.trim();
            page = userRepository
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
        }

       return page.map(AdminMapper::toResponse);

    }

    @Override
    @Transactional
    public AdminListResponse updateUser(Long userId, UpdateAdminRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newEmail = request.email().trim();
        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Long currentUserId = authService.getCurrentUser().id();
        if (currentUserId.equals(userId)
                && user.getRole() == Role.ROLE_ADMIN
                && request.role() != Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove your own admin role");
        }

        if (user.getRole() == Role.ROLE_ADMIN && request.role() != Role.ROLE_ADMIN) {
            long admins = userRepository.countByRole(Role.ROLE_ADMIN);
            if (admins <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one admin must remain");
            }
        }

        user.setEmail(newEmail);
        user.setRole(request.role());

        User saved = userRepository.save(user);
        return AdminMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException((userId)));

        Long currentUserId = authService.getCurrentUser().id();
        if(currentUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete your own user");
        }

        if(user.getRole() == Role.ROLE_ADMIN) {
            long admins = userRepository.countByRole(Role.ROLE_ADMIN);
            if(admins <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one admin must remain");
            }
        }
        footSwipeRepository.deleteByUser_Id(userId);
        footSwipeRepository.deleteByFoot_Owner_Id(userId);

        reviewRepository.deleteByReviewerId(userId);
        reviewRepository.deleteByFoot_Owner_Id(userId);

        footRepository.deleteByOwnerId(userId);
        userRepository.delete(user);
    }

}
