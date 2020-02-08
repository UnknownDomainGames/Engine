package engine.graphics.gl.shader;

public class ShaderFormatException extends RuntimeException {
    public ShaderFormatException() {
        super();
    }

    public ShaderFormatException(String message) {
        super(message);
    }

    public ShaderFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderFormatException(Throwable cause) {
        super(cause);
    }

    protected ShaderFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
