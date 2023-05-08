package pt.up.fe.cpd.proj2.common.message;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public InvalidMessageException(String errorMessage) {
        super(errorMessage);
    }
}
