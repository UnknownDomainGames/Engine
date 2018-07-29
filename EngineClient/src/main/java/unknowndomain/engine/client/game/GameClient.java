package unknowndomain.engine.client.game;

import org.joml.Matrix4d;
import unknowndomain.engine.api.Engine;
import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.block.Grass;
import unknowndomain.engine.client.block.model.*;
import unknowndomain.engine.client.camera.CameraController;
import unknowndomain.engine.client.world.FlatWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class GameClient implements Game {

	private Map<String, World> worldsMap = new HashMap<>();
	
	private Camera camera;
	private CameraController cameraController;

	private final EngineClient engine;
	
	public GameClient(EngineClient engine) {
		this.engine = engine;
		
		camera=engine.getRenderer().getRendererGame().getCamera();
		cameraController=new CameraController(engine.getKeyBindingManager(),camera);
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
        
	}

	@Override
	public void addWorld(World world) {
		worldsMap.put(world.getName(), world);
	}
}
