package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.RenderingLayer;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.client.display.CameraDefault;
import unknowndomain.engine.client.shader.ShaderProgramDefault;

import org.lwjgl.opengl.GL11;

/**
 * render for the scene
 */
public final class RendererGame extends RenderingLayer {

    private ShaderProgram shader;
    private Camera camera;

    public RendererGame() {
        shader = new ShaderProgramDefault();
        shader.createShader();
        camera = new CameraDefault();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    @Override
    public void render() {
        shader.useShader();
        shader.setUniform("projection", camera.makeProjectionMatrix(854,480));
        shader.setUniform("view", camera.makeViewMatrix());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        super.render();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void onCursorMoved(double x, double y){
        camera.rotate((float)x,(float)y);
    }

    public void destroy() {
        shader.deleteShader();
    }

    public Camera getCamera() {
        return camera;
    }
}
