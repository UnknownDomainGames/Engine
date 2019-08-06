package nullengine.client.asset.exception;

import nullengine.client.asset.AssetURL;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(AssetURL path) {
        super(String.format("Asset not found. URL: %s", path.toFileLocation()));
    }
}
