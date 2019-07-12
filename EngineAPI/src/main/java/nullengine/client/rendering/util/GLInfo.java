package nullengine.client.rendering.util;

public interface GLInfo {
    String getVendor();

    String getRenderer();

    String getVersion();

    String getExtensions();

    String getShadingLanguageVersion();
}
