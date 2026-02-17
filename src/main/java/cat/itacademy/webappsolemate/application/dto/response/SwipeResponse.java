package cat.itacademy.webappsolemate.application.dto.response;

import cat.itacademy.webappsolemate.domain.enums.SwipeAction;

import java.time.LocalDateTime;

public record SwipeResponse(

        Long footId,
        SwipeAction action,
        LocalDateTime createdAt
) {
}
