package cat.itacademy.webappsolemate.application.dto.request;


import cat.itacademy.webappsolemate.domain.enums.SwipeAction;
import jakarta.validation.constraints.NotNull;

public record SwipeRequest(

        @NotNull(message = "Action required")
        SwipeAction action
) { }
