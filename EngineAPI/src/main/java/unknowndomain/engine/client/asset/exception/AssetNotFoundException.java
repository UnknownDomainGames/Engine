package unknowndomain.engine.client.asset.exception;

import unknowndomain.engine.client.asset.AssetPath;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(AssetPath path) {
        super(String.format("Asset not found. Path: %s", path));
    }
}
