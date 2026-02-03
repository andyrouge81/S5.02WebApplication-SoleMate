package cat.itacademy.webappsolemate.common.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {

        super("User with the id "+userId+ " not found");
    }

    public UserNotFoundException (String username) {
        super("User with username "+username+ " not found");
    }
}
