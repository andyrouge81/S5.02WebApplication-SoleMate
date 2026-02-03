package cat.itacademy.webappsolemate.common.exceptions;

public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException() {

      super("You have already review this foot");
    }
}
