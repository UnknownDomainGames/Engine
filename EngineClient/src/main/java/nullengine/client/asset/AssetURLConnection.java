package nullengine.client.asset;

import nullengine.client.asset.source.AssetSourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class AssetURLConnection extends URLConnection {

    private InputStream input;

    protected AssetURLConnection(URL url) {
        super(url);
    }

    public void connect() throws IOException {
        if (!connected) {
            Optional<Path> path = AssetSourceManager.instance().getPath(url.getPath());
            if (path.isPresent()) {
                input = Files.newInputStream(path.get());
                connected = true;
            }
            throw new FileNotFoundException(url.getPath());
        }
    }

    public synchronized InputStream getInputStream()
            throws IOException {
        connect();
        return input;
    }
}
