package cat.itacademy.webappsolemate.application.mappers;


import cat.itacademy.webappsolemate.application.dto.response.AdminListResponse;
import cat.itacademy.webappsolemate.domain.entities.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminMapper {

    public static AdminListResponse toResponse(User user) {
        return new AdminListResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
