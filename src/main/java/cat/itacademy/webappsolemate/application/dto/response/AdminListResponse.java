package cat.itacademy.webappsolemate.application.dto.response;

import cat.itacademy.webappsolemate.domain.enums.Role;


import java.time.LocalDateTime;

public record AdminListResponse (

        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt
){ }
