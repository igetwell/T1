package tech.getwell.t1.throwables;

public class NotStartException extends RuntimeException{

    public NotStartException() {
    }

    public NotStartException(String message) {
        super(message);
    }

    public NotStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotStartException(Throwable cause) {
        super(cause);
    }
}
