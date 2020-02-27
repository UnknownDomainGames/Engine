package engine.graphics.management;

public interface GraphicsBackendFactory {
    String getName();

    GraphicsBackend create();
}
