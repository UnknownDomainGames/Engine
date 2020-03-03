package engine.graphics.backend;

public interface GraphicsBackendFactory {
    String getName();

    GraphicsBackend create();
}
