package unknowndomain.engine.client.game;

import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class GameClient implements Game {
    private final EngineClient engine;
    private Map<String, World> worldsMap = new HashMap<>();
    //    private Transformation transformation;
//    private ShaderProgram shaderProgram;
//    private Camera camera;

    public GameClient(EngineClient engine) {
        this.engine = engine;
//        transformation = new Transformation();

        try {
//            shaderProgram = new ShaderProgram();
//            shaderProgram.createVertexShader(Utils.getVertex());
//            shaderProgram.createFragmentShader(Utils.getFragment());
//            shaderProgram.link();
//            shaderProgram.createUniform("projectionMatrix");
//            shaderProgram.createUniform("modelViewMatrix");
//            shaderProgram.createUniform("texture_sampler");//纹理
            System.out.println("BasicRender INIT");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        camera = new Camera();
    }

    @Override
    public Collection<World> getWorlds() {
        return worldsMap.values();
    }

    @Override
    public World getWorld(String name) {
        return worldsMap.get(name);
    }

    @Override
    public void tick() {
        glClearColor((float) 1, (float) 1, (float) 1, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Collection<World> worlds = getWorlds();
        for (World world : worlds) {
//            Matrix4d viewMatrix = transformation.getViewMatrix(camera);
//            Grass block = (Grass) world.getBlock(0, 0, 0);
//            GameItem gameItem = block.getGameItem();
            // Render the mes for this game item
//            Matrix4d modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
//            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
//            System.out.println(block);
        }

    }

    @Override
    public void addWorld(World world) {
//        worldsMap.put(world.getName(), world);
    }


    @Override
    public RuntimeObject createObject(Game context) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }
}
