package cat.itacademy.webappsolemate.application.services.swipe;

import cat.itacademy.webappsolemate.application.dto.request.SwipeRequest;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;
import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.application.mappers.FootSwipeMapper;
import cat.itacademy.webappsolemate.application.services.auth.AuthService;
import cat.itacademy.webappsolemate.common.exceptions.FootNotFoundException;
import cat.itacademy.webappsolemate.common.exceptions.UserNotFoundException;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import cat.itacademy.webappsolemate.domain.entities.FootSwipe;
import cat.itacademy.webappsolemate.domain.entities.User;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.FootSwipeRepository;
import cat.itacademy.webappsolemate.infraestructure.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FootSwipeServiceImpl implements FootSwipeService{

    private final FootSwipeRepository footSwipeRepository;
    private final FootRepository footRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public FootSwipeServiceImpl(FootSwipeRepository footSwipeRepository,
                                FootRepository footRepository,
                                UserRepository userRepository,
                                AuthService authService) {
        this.footSwipeRepository = footSwipeRepository;
        this.footRepository = footRepository;
        this.userRepository =userRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public SwipeResponse saveOrUpdateSwipe(Long footId, SwipeRequest request) {

        CurrentUserResponse currentUser = authService.getCurrentUser();

        User user = userRepository.findById(currentUser.id())
                .orElseThrow(()-> new UserNotFoundException(currentUser.id()));

        Foot foot = footRepository.findById(footId)
                .orElseThrow(() -> new FootNotFoundException(footId));

        FootSwipe swipe = footSwipeRepository.findByUser_IdAndFoot_Id(user.getId(), foot.getId())
                .orElseGet(()-> FootSwipe.builder()
                        .user(user)
                        .foot(foot)
                        .build());

        swipe.setAction(request.action());

        FootSwipe saved = footSwipeRepository.save(swipe);
        return FootSwipeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SwipeResponse> getMySwipes() {
        CurrentUserResponse currentUser = authService.getCurrentUser();

        return footSwipeRepository.findByUser_IdOrderByCreatedAtDesc(currentUser.id())
                .stream()
                .map(FootSwipeMapper::toResponse)
                .toList();
    }

}

