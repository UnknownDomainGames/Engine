package nullengine.client.rendering.gl.shader;

import java.io.InputStream;

public interface ShaderResourceLoader {
    InputStream openStream(String name);
}
