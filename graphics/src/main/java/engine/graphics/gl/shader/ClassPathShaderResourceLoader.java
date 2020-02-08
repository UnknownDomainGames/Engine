package engine.graphics.gl.shader;

import java.io.InputStream;

public class ClassPathShaderResourceLoader implements ShaderResourceLoader {

    public static final ShaderResourceLoader DEFAULT = new ClassPathShaderResourceLoader("shader/");

    private final ClassLoader classLoader;
    private final String parent;

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
