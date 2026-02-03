package cat.itacademy.webappsolemate.application.services.auth;


import cat.itacademy.webappsolemate.application.dto.request.LoginRequest;
import cat.itacademy.webappsolemate.application.dto.request.RegisterRequest;
import cat.itacademy.webappsolemate.application.dto.response.AuthResponse;
import cat.itacademy.webappsolemate.application.dto.response.CurrentUserResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    CurrentUserResponse getCurrentUser();
}
