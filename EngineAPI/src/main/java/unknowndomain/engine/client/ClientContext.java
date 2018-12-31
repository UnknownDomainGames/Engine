package unknowndomain.engine.client;

import org.joml.FrustumIntersection;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.player.Player;

public interface ClientContext {

    Camera getCamera();

    void setCamera(Camera camera);

    GameWindow getWindow();

    TextureManager getTextureManager();

    double partialTick();

    Thread getRenderThread();

    default boolean isRenderThread() {
        return Thread.currentThread() == getRenderThread();
    }

    FrustumIntersection getFrustumIntersection();

    Player getPlayer();

    BlockPrototype.Hit getHit();
}
