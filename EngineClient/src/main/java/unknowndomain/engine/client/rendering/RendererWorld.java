package unknowndomain.engine.client.rendering;

import java.util.Collection;

import org.joml.Matrix4d;

import org.lwjgl.opengl.GL20;
import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.block.Grass;
import unknowndomain.engine.client.block.model.GameItem;
import unknowndomain.engine.client.block.model.ShaderProgram;
import unknowndomain.engine.client.block.model.Transformation;
import unknowndomain.engine.client.block.model.Utils;
import unknowndomain.engine.client.shader.ShaderProgramDefault;
import unknowndomain.engine.client.util.OpenGLHelper;
import unknowndomain.engine.client.world.FlatWorld;

public class RendererWorld implements Renderer{
	private static final float FOV = (float) Math.toRadians(60.0f);//视角
    private static final float Z_NEAR = 0.01f;//Z最小值
    private static final float Z_FAR = 1000.f;//Z最大值
    private ShaderProgram shaderProgram;
    private ShaderProgramDefault shader;
    private Transformation transformation;
    // TODO: hard code.
    public RendererWorld() {
    	transformation=new Transformation();
		shader = new ShaderProgramDefault();
		shader.createShader();
		GL20.glUseProgram(0);
//		try {
//			shaderProgram =new ShaderProgram();
//	    	shaderProgram.createVertexShader(Utils.getVertex());
//	        shaderProgram.createFragmentShader(Utils.getFragment());
//	        shaderProgram.link();
//	        shaderProgram.createUniform("projectionMatrix");
//	        shaderProgram.createUniform("modelViewMatrix");
//	        shaderProgram.createUniform("texture_sampler");//纹理
//	    	}catch(Exception ex) {
//	    		ex.printStackTrace();
//	    	}
    }
    @Override
    public void render() {
    	EngineClient client=UnknownDomain.getEngine();
    	if(client==null)return;
    	Game game=client.getGame();
    	for(World world : game.getWorlds()){
    		
    		FlatWorld flatWorld=(FlatWorld)world;
        	Collection<Block> allBlock=flatWorld.getAllBlock();
//        	shaderProgram.bind();
			OpenGLHelper.useShaderProgram(shader);
        	//shader.useShader();

            // Update projection Matrix
            Matrix4d projectionMatrix = transformation.getProjectionMatrix(FOV, client.getGameWindow().getWidth(), client.getGameWindow().getHeight(), Z_NEAR, Z_FAR);
//            shaderProgram.setUniform("projectionMatrix", projectionMatrix);
            shader.setUniform("projection", projectionMatrix);

            // Update view Matrix

            
//            shaderProgram.setUniform("texture_sampler", 0);
//            shader.setUniform("texImage", 0);

            Matrix4d viewMatrix = transformation.getViewMatrix(client.getRenderer().getRendererGame().getCamera());
            for(Block block:allBlock) {
            	Grass grass=(Grass) block;
            	GameItem gameItem=grass.getGameItem();
                // Render the mes for this game item
//            	Matrix4d modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
//                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
				Matrix4d modelMatrix = transformation.getModelMatrix(gameItem);
                shader.setUniform("model", modelMatrix);
                shader.setUniform("view", viewMatrix);
                gameItem.getMesh().render();
        	}
//			shaderProgram.unbind();
        }
    }
}
