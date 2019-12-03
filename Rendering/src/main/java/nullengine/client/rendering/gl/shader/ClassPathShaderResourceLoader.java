package nullengine.client.rendering.gl.shader;

import java.io.InputStream;

public class ClassPathShaderResourceLoader implements ShaderResourceLoader {

    private final ClassLoader classLoader;
    private final String parent;

    public ClassPathShaderResourceLoader() {
        this("shader/");
    }

    public ClassPathShaderResourceLoader(String parent) {
        this.classLoader = getClass().getClassLoader();
        this.parent = parent;
    }

    public ClassPathShaderResourceLoader(ClassLoader classLoader, String parent) {
        this.classLoader = classLoader;
        this.parent = parent;
    }

    @Override
    public InputStream openStream(String name) {
        return classLoader.getResourceAsStream(parent + name);
    }
}
