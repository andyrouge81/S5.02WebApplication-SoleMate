package cat.itacademy.webappsolemate.application.mappers;

import cat.itacademy.webappsolemate.application.dto.response.FootResponse;
import cat.itacademy.webappsolemate.domain.entities.Foot;
import org.springframework.stereotype.Component;

@Component
public class FootMapper {

    private FootMapper() {}

    public static FootResponse toResponse(Foot foot) {
        return new FootResponse(
                foot.getId(),
                foot.getNickname(),
                foot.getImageUrl(),
                foot.getArchType(),
                foot.getOwner().getUsername(),
                foot.getCreatedAt()
        );
    }

}
