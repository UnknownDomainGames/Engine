package engine.client.asset.exception;

public class AssetLoadException extends RuntimeException {

    public AssetLoadException(String message) {
        super(message);
    }

    public AssetLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
