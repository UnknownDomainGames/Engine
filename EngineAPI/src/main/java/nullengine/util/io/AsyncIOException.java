package nullengine.util.io;

public class AsyncIOException extends RuntimeException {

    public AsyncIOException() {
    }

    public AsyncIOException(String message) {
        super(message);
    }

    public AsyncIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncIOException(Throwable cause) {
        super(cause);
    }
}
