package cat.itacademy.webappsolemate.application.dto.request;

import cat.itacademy.webappsolemate.domain.enums.ArchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FootRequest(

        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        String imageUrl,

        @NotNull
        ArchType archType
) {
}
