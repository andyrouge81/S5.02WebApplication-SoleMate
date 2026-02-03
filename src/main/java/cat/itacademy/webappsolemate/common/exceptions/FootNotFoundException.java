package cat.itacademy.webappsolemate.common.exceptions;

public class FootNotFoundException extends RuntimeException {
    public FootNotFoundException(Long footId) {

        super("Foot with id "+footId+ " not found");
    }
}
