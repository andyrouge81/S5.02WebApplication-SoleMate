package cat.itacademy.webappsolemate.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(

        @NotBlank
        String comment,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rateAspect
) {
}
