package cat.itacademy.webappsolemate.application.dto.response;

import java.time.Instant;

public record ApiError(

        String errorType,
        String message,
        Instant timestamp
) {
}
