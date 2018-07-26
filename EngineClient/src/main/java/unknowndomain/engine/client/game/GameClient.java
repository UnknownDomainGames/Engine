package unknowndomain.engine.client.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.block.Grass;
import unknowndomain.engine.client.block.model.Camera;
import unknowndomain.engine.client.block.model.GameItem;
import unknowndomain.engine.client.block.model.ShaderProgram;
import unknowndomain.engine.client.block.model.Transformation;
import unknowndomain.engine.client.block.model.Utils;

public class GameClient implements Game {

	private Map<String, World> worldsMap = new HashMap<>();
	
	private Transformation transformation;
	private ShaderProgram shaderProgram;
	private Camera camera;
	
	public GameClient() {
		transformation=new Transformation();
		
		try {
			shaderProgram =new ShaderProgram();
	    	shaderProgram.createVertexShader(Utils.getVertex());
	        shaderProgram.createFragmentShader(Utils.getFragment());
	        shaderProgram.link();
	        shaderProgram.createUniform("projectionMatrix");
	        shaderProgram.createUniform("modelViewMatrix");
	        shaderProgram.createUniform("texture_sampler");//纹理
			System.out.println("BasicRender INIT");
	    	}catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
		camera=new Camera();
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
		glClearColor((float)1, (float)1, (float)1,1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        Collection<World> worlds=getWorlds();
        for(World world:worlds) {
        	Matrix4d viewMatrix = transformation.getViewMatrix(camera);
        	Grass block=(Grass) world.getBlock(0, 0, 0);
        	GameItem gameItem=block.getGameItem();
            // Render the mes for this game item
        	Matrix4d modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            
            gameItem.getMesh().render();
        	System.out.println(block);
        }
        
	}

	@Override
	public void addWorld(World world) {
		worldsMap.put(world.getName(), world);
	}
	
	
}
