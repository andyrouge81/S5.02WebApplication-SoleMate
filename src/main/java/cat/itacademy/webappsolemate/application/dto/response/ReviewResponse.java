package cat.itacademy.webappsolemate.application.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(

        Long id,
        String comment,
        Integer rateAspect,
        String reviewUsername,
        LocalDateTime createdAt
) { }
