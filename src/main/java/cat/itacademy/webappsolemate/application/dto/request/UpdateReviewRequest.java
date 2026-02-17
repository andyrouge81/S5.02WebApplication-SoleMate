package cat.itacademy.webappsolemate.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(

        @NotBlank(message = "Comment must not be empty")
        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment,

        @Min(value = 1, message = "Rate must not be less than 1")
        @Max(value = 5, message = "Rate must be between 1 and 5")
        Integer rateAspect
) {
}
