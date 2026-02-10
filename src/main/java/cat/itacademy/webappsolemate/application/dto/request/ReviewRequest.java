package cat.itacademy.webappsolemate.application.dto.request;

import jakarta.validation.constraints.*;

public record ReviewRequest(

        @NotBlank
        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rateAspect
) {
}
