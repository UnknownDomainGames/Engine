package engine.client.asset.exception;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(String path) {
        super(String.format("Asset not found. Path: %s", path));
    }
}
