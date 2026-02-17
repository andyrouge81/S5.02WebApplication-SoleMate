package cat.itacademy.webappsolemate.application.services.swipe;


import cat.itacademy.webappsolemate.application.dto.request.SwipeRequest;
import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.domain.enums.SwipeAction;

import java.util.List;

public interface FootSwipeService {

    SwipeResponse saveOrUpdateSwipe(Long footId, SwipeRequest request);

    List<SwipeResponse> getMySwipes();
}
