package cat.itacademy.webappsolemate.common.exceptions;

public class DuplicateFootException extends RuntimeException {
    public DuplicateFootException() {

        super("This foot already exists. Add a review on the existing feet post");
    }

    public DuplicateFootException(Long footId) {
        super("This foot already exists. Foot id: "+footId);
    }
}
