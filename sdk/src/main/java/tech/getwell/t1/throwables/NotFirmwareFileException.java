package tech.getwell.t1.throwables;

public class NotFirmwareFileException extends RuntimeException{

    public NotFirmwareFileException() {
    }

    public NotFirmwareFileException(String message) {
        super(message);
    }

    public NotFirmwareFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFirmwareFileException(Throwable cause) {
        super(cause);
    }
}
