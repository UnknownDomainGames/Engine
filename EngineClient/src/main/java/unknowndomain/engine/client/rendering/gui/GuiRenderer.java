package unknowndomain.engine.client.rendering.gui;

import org.joml.*;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.gui.Container;
import unknowndomain.engine.client.gui.Graphics;
import unknowndomain.engine.client.gui.Scene;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.font.TTFontHelper;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.util.Color;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;

/**
 * render for any gui
 */
public class GuiRenderer implements Renderer {

    private final ShaderProgram shader;

    private final int u_ProjMatrix, u_WindowSize, u_ClipRect, u_RenderText;

    private final TTFontHelper fontHelper;
    private final Graphics graphics;

    private Scene guiScene;
    private final List<Scene> hudScene = new LinkedList<>();

    private ClientContext context;

    public GuiRenderer(ByteBuffer defaultFontData, Shader vertexShader, Shader fragShader) {
        shader = new ShaderProgram();
        shader.init(vertexShader, fragShader);
        u_ProjMatrix = shader.getUniformLocation("u_ProjMatrix");
        u_WindowSize = shader.getUniformLocation("u_WindowSize");
        u_ClipRect = shader.getUniformLocation("u_ClipRect");
        u_RenderText = shader.getUniformLocation("u_RenderText");

        this.fontHelper = new TTFontHelper(() -> {
            glEnable(GL_TEXTURE_2D);
            setUniform(u_RenderText, true);
        }, () -> {
            glDisable(GL_TEXTURE_2D);
            setUniform(u_RenderText, false);
        });
        this.graphics = new GraphicsImpl(this.fontHelper);
        graphics.setFont(fontHelper.loadNativeFont(defaultFontData, 16).getFont());

    }

    @Override
    public void init(ClientContext context) {
        this.context = context;
    }

    @Override
    public void render() {
        startRender();

        // render scene
        if (guiScene != null)
            renderScene(guiScene);

        for (Scene scene : hudScene) {
            renderScene(scene);
        }

        debug(context);

        endRender();
    }


    private void startRender() {
        shader.use();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        resize();
    }


    private void resize() {
        if (context.getWindow().isResized()) {
            int width = context.getWindow().getWidth(), height = context.getWindow().getHeight();
            setUniform(u_ProjMatrix, new Matrix4f().setOrtho(0, width, height, 0, 1, -1));
            setUniform(u_WindowSize, new Vector2f(width, height));
            setUniform(u_ClipRect, new Vector4f(0, 0, width, height));
        }
    }

    private void endRender() {
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

    private void debug(ClientContext context) {
        updateFPS();

        graphics.setColor(Color.WHITE);

        int middleX = context.getWindow().getWidth() / 2, middleY = context.getWindow().getHeight() / 2;
        graphics.drawRect(middleX - 5, middleY - 5, 10, 10);
        graphics.drawLine(middleX, middleY - 10, middleX, middleY + 10);
        graphics.drawLine(middleX - 10, middleY, middleX + 10, middleY);

        Entity player = UnknownDomain.getGame().getPlayer().getControlledEntity();
//        AABBd box = AABBs.translate(player.getBoundingBox(), player.getPosition(), new AABBd());

        graphics.drawText("FPS: " + displayFPS, 0, 0);
        graphics.drawText(String.format("Player location: %f, %f, %f", player.getPosition().x, player.getPosition().y, player.getPosition().z), 0, 19);
        graphics.drawText(String.format("Player motion: %f, %f, %f", player.getMotion().x, player.getMotion().y, player.getMotion().z), 0, 38);
        graphics.drawText(String.format("Player yaw: %f, pitch: %f, roll: %f", player.getRotation().x, player.getRotation().y, player.getRotation().z), 0, 19 * 3);
        graphics.drawText(String.format("Chunk: %d, %d, %d", (int) player.getPosition().x >> 4, (int) player.getPosition().y >> 4, (int) player.getPosition().z >> 4), 0, 19 * 4);
//        fontRenderer.renderText(String.format("Player bounding box: %s", box.toString(new DecimalFormat("#.##"))), 0, 19 * 3, 0xffffffff);
        //fontRenderer.renderText(player.getBehavior(Entity.TwoHands.class).getMainHand().getLocalName(), 0, 64, 0xffffffff, 16);

        BlockPrototype.Hit hit = context.getHit();
        if (hit != null) {
            Vector3f hitedPos = new Vector3f(hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ());
            AABBd blockAABB = AABBs.translate(hit.getBlock().getBoundingBoxes()[0], hitedPos, new AABBd());
            graphics.drawText(String.format("Looking block: %s", hit.getBlock().getUniqueName()), 0, 19 * 10);
            graphics.drawText(String.format("Looking pos: %s(%d, %d, %d)", hit.getFace().name(), hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ()), 0, 19 * 11);
            graphics.drawText(String.format("Looking at: (%f, %f, %f)", hit.getHit().x, hit.getHit().y, hit.getHit().z), 0, 19 * 12);
            graphics.drawText(String.format("Bounding box: %s", blockAABB.toString(new DecimalFormat("#.##"))), 0, 19 * 13);
//            fontRenderer.renderText(String.format("Collided with the looking box: %s", blockAABB.testAABB(box)), 0, 19*12, 0xffffffff);

            //fontRenderer.renderText(String.format("[%f, %f, %f, %f, %f, %f]", box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), 0, 0, 0xffffffff, 32);
            //fontRenderer.renderText(String.format("[%f, %f, %f, %f, %f, %f]", blockAABB.minX, blockAABB.minY, blockAABB.minZ, blockAABB.maxX, blockAABB.maxY, blockAABB.maxZ), 0, 35, 0xffffffff, 32);
            //fontRenderer.renderText(String.format("%s", blockAABB.testAABB(box) ? "coll" : "fine"), 0, 69, 0xffffffff, 32);
        }
    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
