package engine.graphics.gl.shader;

import java.io.InputStream;

public interface ShaderResourceLoader {
    InputStream openStream(String name);
}
