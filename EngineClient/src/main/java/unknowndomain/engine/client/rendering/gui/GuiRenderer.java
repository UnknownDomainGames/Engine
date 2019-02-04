package unknowndomain.engine.client.rendering.gui;

import org.joml.*;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.game.ClientContextImpl;
import unknowndomain.engine.client.gui.Container;
import unknowndomain.engine.client.gui.DebugHUD;
import unknowndomain.engine.client.gui.Scene;
import unknowndomain.engine.client.gui.internal.FontHelper;
import unknowndomain.engine.client.gui.internal.ImageHelper;
import unknowndomain.engine.client.gui.internal.Internal;
import unknowndomain.engine.client.gui.rendering.Graphics;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.font.TTFontHelper;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.math.AABBs;
import unknowndomain.engine.util.Color;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class GuiRenderer implements Renderer {

    private final ShaderProgram shader;

    private final TTFontHelper fontHelper;
    private final Graphics graphics;

    private DebugHUD debugHUD;

    private ClientContext context;

    public GuiRenderer(ByteBuffer defaultFontData, Shader vertexShader, Shader fragShader) {
        shader = ShaderManager.INSTANCE.createShader("gui_shader", vertexShader, fragShader);

        this.fontHelper = new TTFontHelper(() -> {
            glEnable(GL_TEXTURE_2D);
            ShaderManager.INSTANCE.setUniform("u_RenderText", true);
        }, () -> {
            glDisable(GL_TEXTURE_2D);
            ShaderManager.INSTANCE.setUniform("u_RenderText", false);
        });
        Font defaultFont = fontHelper.loadNativeFont(defaultFontData, 16).getFont();
        fontHelper.setDefaultFont(defaultFont);

        this.graphics = new GraphicsImpl(this);
        graphics.setFont(defaultFont);
    }

    @Override
    public void init(ClientContext context) {
        this.context = context;
        Internal.setContext(new Internal.Context() {
            @Override
            public FontHelper getFontHelper() {
                return fontHelper;
            }

            @Override
            public ImageHelper getImageHelper() {
                return new ImageHelper() {
                    @Override
                    public GLTexture getTexture(ResourcePath path) {
                        return context.getTextureManager().getTexture(path);
                    }
                };
            }
        });
//        VBox vBox = new VBox();
//        vBox.spacing().set(5);
//        vBox.getChildren().add(new Text("Hello GUI!"));
//        vBox.getChildren().add(new Text("Bye GUI!"));
//        Image image = new Image(new ResourcePath("texture", "/assets/unknowndomain/textures/block/grass_side.png"));
//        image.imageWidth().set(200);
//        image.imageHeight().set(200);
//        vBox.getChildren().add(image);
//        Scene debug = new Scene(vBox);
//        ((ClientContextImpl) context).getGuiManager().showHud("test", debug);
//        AnchorPane pane = new AnchorPane();
//        for (int i = 0; i < 10; i++) {
//            var button = new Button(String.format("YVES %02d", i));
//            button.buttonwidth().set(100);
//            AnchorPane.setLeftAnchor(button, 50f * i + 10);
//            AnchorPane.setTopAnchor(button, 40f * i);
//            pane.getChildren().add(button);
//        }
//        Scene screen = new Scene(pane);
//        ((ClientContextImpl) context).getGuiManager().showIncognitoScreen(screen);
        debugHUD = new DebugHUD();
        ((ClientContextImpl) context).getGuiManager().showHud("debug", new Scene(debugHUD));
    }

    @Override
    public void render() {
        startRender();

        // render scene
        if (context instanceof ClientContextImpl) { //TODO: stupid check
            var ci = (ClientContextImpl) context;
            for (Scene scene : ci.getGuiManager().getHuds().values()) {
                renderScene(scene);
            }
            if (ci.getGuiManager().getDisplayingScreen() != null) {
                renderScene(ci.getGuiManager().getDisplayingScreen());
            }
        }

        debug(context);

        endRender();
    }

    public TTFontHelper getFontHelper() {
        return fontHelper;
    }

    public void setClipRect(Vector4fc clipRect) {
        ShaderManager.INSTANCE.setUniform("u_ClipRect", clipRect);
    }

    private void startRender() {
        ShaderManager.INSTANCE.bindShader(shader);

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
            ShaderManager.INSTANCE.setUniform("u_ProjMatrix", new Matrix4f().setOrtho(0, width, height, 0, 1000, -1000));
            ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f());
            ShaderManager.INSTANCE.setUniform("u_WindowSize", new Vector2f(width, height));
            ShaderManager.INSTANCE.setUniform("u_ClipRect", new Vector4f(0, 0, width, height));
        }
    }

    private void endRender() {
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
    }

    private void renderScene(Scene scene) {
        if (context.getWindow().isResized()) {
            scene.setSize(context.getWindow().getWidth(), context.getWindow().getHeight());
        }

        scene.update();

        Container root = scene.getRoot();
        if (!root.visible().get())
            return;

        graphics.pushClipRect(0, 0, scene.width().get(), scene.height().get());
        root.getRenderer().render(root, graphics);
        graphics.popClipRect();
    }

    private void debug(ClientContext context) {
        graphics.setColor(Color.WHITE);

        // TODO: CrossHair, move it.
        int middleX = context.getWindow().getWidth() / 2, middleY = context.getWindow().getHeight() / 2;
        graphics.drawLine(middleX, middleY - 10, middleX, middleY + 10);
        graphics.drawLine(middleX - 10, middleY, middleX + 10, middleY);

        debugHUD.update(context);

        RayTraceBlockHit hit = context.getHit();
        if (hit != null) {
            Vector3f hitedPos = new Vector3f(hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ());
            AABBd blockAABB = AABBs.translate(hit.getBlock().getBoundingBoxes()[0], hitedPos, new AABBd());
            graphics.drawText(String.format("Looking block: %s", hit.getBlock().getUniqueName()), 0, 19 * 10);
            graphics.drawText(String.format("Looking pos: %s(%d, %d, %d)", hit.getFace().name(), hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ()), 0, 19 * 11);
            graphics.drawText(String.format("Looking at: (%f, %f, %f)", hit.getHitPoint().x, hit.getHitPoint().y, hit.getHitPoint().z), 0, 19 * 12);
            graphics.drawText(String.format("Bounding box: %s", blockAABB.toString(new DecimalFormat("#.##"))), 0, 19 * 13);
//            fontRenderer.renderText(String.format("Collided with the looking box: %s", blockAABB.testAABB(box)), 0, 19*12, 0xffffffff);

            //fontRenderer.renderText(String.format("[%f, %f, %f, %f, %f, %f]", box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), 0, 0, 0xffffffff, 32);
            //fontRenderer.renderText(String.format("[%f, %f, %f, %f, %f, %f]", blockAABB.minX, blockAABB.minY, blockAABB.minZ, blockAABB.maxX, blockAABB.maxY, blockAABB.maxZ), 0, 35, 0xffffffff, 32);
            //fontRenderer.renderText(String.format("%s", blockAABB.testAABB(box) ? "coll" : "fine"), 0, 69, 0xffffffff, 32);
        }

//        testBlockRenderer();
    }


//    private void testBlockRenderer() {
//        glDisable(GL_LINE_SMOOTH);
//        glDisable(GL_POINT_SMOOTH);
//        glDisable(GL_POLYGON_SMOOTH);
//        setUniform(u_ModelMatrix, new Matrix4f()
////                .rotate(30, 1, 0, 0)
////                .rotate(-30, 0, 1, 0)
//                .translate(context.getWindow().getWidth() - 75, context.getWindow().getHeight() - 25, 0)
////                .translate(-0.5f, -0.5f, -0.5f)
////                .rotate((float) Math.toRadians(180), 0, 0, 1)
//                .rotate((float) Math.toRadians(195), 1, 0, 0)
//                .rotate((float) Math.toRadians(-30), 0, 1, 0)
//                .scale(25));
////                .translate(50, 50, 0)
////                .rotate(-30, 0, 1, 0)
////                .rotate(165, 1, 0, 0));
//
//        glEnable(GL11.GL_DEPTH_TEST);
//        glEnable(GL11.GL_CULL_FACE);
//        glEnable(GL11.GL_TEXTURE_2D);
//        setUniform(u_UsingTexture, true);
//        context.getTextureManager().getTextureAtlas(BLOCK).bind();
//
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder buffer = tessellator.getBuffer();
//        buffer.begin(GL_TRIANGLES, true, true, true);
//        blockRenderer.render(Blocks.GRASS, buffer);
//
//        tessellator.draw();
//
//        setUniform(u_UsingTexture, false);
//        glBindTexture(GL_TEXTURE_2D, 0);
//        glDisable(GL11.GL_CULL_FACE);
//        glDisable(GL11.GL_TEXTURE_2D);
//        glDisable(GL11.GL_DEPTH_TEST);
//
//        setUniform(u_ModelMatrix, new Matrix4f());
//    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
