package cat.itacademy.webappsolemate.application.mappers;

import cat.itacademy.webappsolemate.application.dto.response.SwipeResponse;
import cat.itacademy.webappsolemate.domain.entities.FootSwipe;

public class FootSwipeMapper {

    public static SwipeResponse toResponse(FootSwipe footSwipe) {
        return new SwipeResponse(
                footSwipe.getFoot().getId(),
                footSwipe.getAction(),
                footSwipe.getCreatedAt()
        );
    }
}
