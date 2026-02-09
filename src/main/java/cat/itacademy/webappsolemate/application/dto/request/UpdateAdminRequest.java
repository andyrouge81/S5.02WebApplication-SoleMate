package cat.itacademy.webappsolemate.application.dto.request;

import cat.itacademy.webappsolemate.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAdminRequest(

        @Email(message = "Invalid Email")
        @NotBlank
        @Size(max = 100, message = "The email could not be more than 100 characters")
        String email,

        @NotNull(message = "Rol must not be empty")
        Role role
) {
}
