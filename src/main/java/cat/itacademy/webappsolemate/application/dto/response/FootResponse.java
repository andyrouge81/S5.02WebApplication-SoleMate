package cat.itacademy.webappsolemate.application.dto.response;

import cat.itacademy.webappsolemate.domain.enums.ArchType;

import java.time.LocalDateTime;

public record FootResponse(

        Long id,
        String nickname,
        String imageUrl,
        ArchType archType,
        String ownerUsername,
        LocalDateTime createdAt
) {
}
