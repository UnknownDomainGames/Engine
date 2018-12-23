package unknowndomain.engine.client.rendering.gui;

import org.joml.AABBd;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.gui.Container;
import unknowndomain.engine.client.gui.Graphics;
import unknowndomain.engine.client.gui.Scene;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.font.FontRenderer;
import unknowndomain.engine.client.rendering.gui.font.TTFFontRenderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.world.World;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;

/**
 * render for any gui
 */
public class GuiRenderer implements Renderer {

    private final ShaderProgram shader;

    private final int u_ProjMatrix, u_WindowSize, u_ClipRect, u_UsingAlpha;

    private final FontRenderer fontRenderer;
    private final Graphics graphics;

    private Scene guiScene;
    private final List<Scene> hudScene = new LinkedList<>();

    private RenderContext context;

    public GuiRenderer(ByteBuffer fontRenderer, Shader vertexShader, Shader fragShader) {
        this.fontRenderer = new TTFFontRenderer(fontRenderer);
        this.graphics = new GraphicsImpl(this.fontRenderer);
        shader = new ShaderProgram();
        shader.init(vertexShader, fragShader);
        u_ProjMatrix = shader.getUniformLocation("u_ProjMatrix");
        u_WindowSize = shader.getUniformLocation("u_WindowSize");
        u_ClipRect = shader.getUniformLocation("u_ClipRect");
        u_UsingAlpha = shader.getUniformLocation("u_UsingAlpha");
    }

    @Override
    public void init(RenderContext context) {
        this.context = context;
    }

    @Override
    public void render() {
        shader.use();

        if (context.getWindow().isResized()) {
            int width = context.getWindow().getWidth(), height = context.getWindow().getHeight();
            setUniform(u_ProjMatrix, new Matrix4f().setOrtho(0, width, height, 0, 1, -1));
            setUniform(u_WindowSize, new Vector2f(width, height));
            setUniform(u_ClipRect, new Vector4f(0, 0, width, height));
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        setUniform(u_UsingAlpha, true);

        // render scene
        if (guiScene != null)
            renderScene(guiScene);

        for (Scene scene : hudScene) {
            renderScene(scene);
        }

        debug(context);

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
    }

    private void renderScene(Scene scene) {
        Container root = scene.getRoot();
        if (!root.isVisible())
            return;

        root.getRenderer().render(graphics);
    }

    private long lastFPS = getTime();
    private int fps = 0;
    private int displayFPS = 0;

    public long getTime() {
        return System.nanoTime() / 1_000_000;
    }

    public void updateFPS() {
        long time = getTime();
        if (time - lastFPS > 1000) {
            displayFPS = fps;
            fps = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        fps++;
    }

    private void debug(RenderContext context) {
        updateFPS();
        Entity player = UnknownDomain.getGame().getPlayer().getMountingEntity();
        AABBd box = AABBs.translate(player.getBoundingBox(), player.getPosition(), new AABBd());
        World world = UnknownDomain.getGame().getPlayer().getWorld();

//        BlockPrototype.Hit hit = world.raycast(context.getCamera().getPosition(),
//                context.getCamera().getFrontVector(), 5);
        fontRenderer.drawText("FPS: " + displayFPS, 0, 0, 0xffffffff);
        fontRenderer.drawText(String.format("Player location: %f, %f, %f", player.getPosition().x, player.getPosition().y, player.getPosition().z), 0, 19, 0xffffffff);
        fontRenderer.drawText(String.format("Player motion: %f, %f, %f", player.getMotion().x, player.getMotion().y, player.getMotion().z), 0, 38, 0xffffffff);
        fontRenderer.drawText(String.format("Player yaw: %f, pitch: %f, roll: %f", player.getRotation().x, player.getRotation().y, player.getRotation().z), 0, 19 * 3, 0xffffffff);
        fontRenderer.drawText(String.format("Chunk: %d, %d, %d", (int) player.getPosition().x >> 4, (int) player.getPosition().y >> 4, (int) player.getPosition().z >> 4), 0, 19 * 4, 0xffffffff);
//        fontRenderer.drawText(String.format("Player bounding box: %s", box.toString(new DecimalFormat("#.##"))), 0, 19 * 3, 0xffffffff);
        //fontRenderer.drawText(player.getBehavior(Entity.TwoHands.class).getMainHand().getLocalName(), 0, 64, 0xffffffff, 16);

//        if (hit != null) {
//
//            Vector3f hitedPos = new Vector3f(hit.position.getX(), hit.position.getY(), hit.position.getZ());
//            AABBd blockAABB = AABBs.translate(hit.block.getBoundingBoxes()[0], hitedPos, new AABBd());
//            fontRenderer.drawText(String.format("Looking at: %f,%f,%f", hitedPos.x, hitedPos.y, hitedPos.z), 0, 85, 0xffffffff);
//            fontRenderer.drawText(String.format("bounding box: %s", blockAABB.toString(new DecimalFormat("#.##"))), 0, 105, 0xffffffff);
//            fontRenderer.drawText(String.format("Collided with the looking box: %s", blockAABB.testAABB(box)), 0, 125, 0xffffffff);
//
//            //fontRenderer.drawText(String.format("[%f, %f, %f, %f, %f, %f]", box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), 0, 0, 0xffffffff, 32);
//            //fontRenderer.drawText(String.format("[%f, %f, %f, %f, %f, %f]", blockAABB.minX, blockAABB.minY, blockAABB.minZ, blockAABB.maxX, blockAABB.maxY, blockAABB.maxZ), 0, 35, 0xffffffff, 32);
//            //fontRenderer.drawText(String.format("%s", blockAABB.testAABB(box) ? "coll" : "fine"), 0, 69, 0xffffffff, 32);
//        }
    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
