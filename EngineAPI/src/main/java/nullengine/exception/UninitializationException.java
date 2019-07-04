package nullengine.exception;

import java.util.function.Supplier;

public class UninitializationException extends RuntimeException {

    public static <T> Supplier<T> supplier(String message) {
        return () -> {
            throw new UninitializationException(message);
        };
    }

    public UninitializationException(String message) {
        super(message);
    }

    public UninitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UninitializationException(Throwable cause) {
        super(cause);
    }
}
