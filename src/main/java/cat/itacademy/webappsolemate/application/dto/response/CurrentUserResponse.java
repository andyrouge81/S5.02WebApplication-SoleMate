package cat.itacademy.webappsolemate.application.dto.response;

import cat.itacademy.webappsolemate.domain.enums.Role;

public record CurrentUserResponse(

        Long id,
        String username,
        Role role

) { }
