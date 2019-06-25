package nullengine.client.asset.exception;

public class AssetLoadException extends RuntimeException {

    public AssetLoadException(String message) {
        super(message);
    }

    public AssetLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetLoadException(Throwable cause) {
        super(cause);
    }
}
