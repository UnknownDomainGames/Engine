package nullengine.client.asset;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class AssetURLStreamHandler extends URLStreamHandler {

    public static void initialize() {
        URL.setURLStreamHandlerFactory(protocol -> {
            if ("asset".equals(protocol)) return new AssetURLStreamHandler();
            return null;
        });
    }

    @Override
    protected URLConnection openConnection(URL u) {
        return new AssetURLConnection(u);
    }
}
