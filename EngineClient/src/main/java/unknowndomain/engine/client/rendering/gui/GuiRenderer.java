package unknowndomain.engine.client.rendering.gui;

import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.Container;
import unknowndomain.engine.client.gui.GuiManager;
import unknowndomain.engine.client.gui.Scene;
import unknowndomain.engine.client.gui.internal.FontHelper;
import unknowndomain.engine.client.gui.internal.ImageHelper;
import unknowndomain.engine.client.gui.internal.Internal;
import unknowndomain.engine.client.gui.rendering.Graphics;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.display.Window;
import unknowndomain.engine.client.rendering.gui.font.TTFontHelper;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.shader.ShaderProgramBuilder;
import unknowndomain.engine.client.rendering.shader.ShaderType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class GuiRenderer implements Renderer {

    private Window window;
    private GuiManager guiManager;

    private ObservableValue<ShaderProgram> shader;

    private TTFontHelper fontHelper;
    private Graphics graphics;

    @Override
    public void init(RenderContext context) {
        this.guiManager = context.getGuiManager();
        this.window = context.getWindow();
        var textureManager = context.getTextureManager();

        shader = ShaderManager.INSTANCE.registerShader("gui_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "gui.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "gui.frag")));

        this.fontHelper = new TTFontHelper(() -> {
            glEnable(GL_TEXTURE_2D);
            ShaderManager.INSTANCE.setUniform("u_RenderText", true);
        }, () -> {
            glDisable(GL_TEXTURE_2D);
            ShaderManager.INSTANCE.setUniform("u_RenderText", false);
        });

        this.graphics = new GraphicsImpl(context, this);

        try {
            byte[] fontDataBytes = Files.readAllBytes(context.getEngine().getAssetManager().getPath(AssetPath.of("engine", "font", "font.ttf")).get());
            ByteBuffer fontData = ByteBuffer.allocateDirect(fontDataBytes.length);
            fontData.put(fontDataBytes);
            fontData.flip();
            Font defaultFont = fontHelper.loadNativeFont(fontData, 16).getFont();
            fontHelper.setDefaultFont(defaultFont);
            graphics.setFont(defaultFont);
        } catch (IOException e) {

        }

        Internal.setContext(new Internal.Context() {
            @Override
            public FontHelper getFontHelper() {
                return fontHelper;
            }

            @Override
            public ImageHelper getImageHelper() {
                return textureManager::getTextureDirect;
            }
        });


//        VBox box = new VBox();
//        box.background().setValue(new Background(new Color(0.1f, 0.1f, 0.1f, 0.9f)));
//        TextField textField = new TextField();
//        textField.promptText().setValue("Hey you suckers!!");
//        textField.fieldwidth().set(200);
//        textField.fieldheight().set(23);
//        Button button = new Button("Button");
//        button.buttonwidth().set(100);
//        button.setOnClick(mouseClickEvent -> guiManager.closeScreen());
//        ToggleButton toggleButton = new ToggleButton("Toggle");
//        toggleButton.buttonwidth().set(100);
//        box.getChildren().addAll(textField, button, toggleButton);
//        button.border().setValue(new Border(Color.GREEN,5f));
//        button.buttonbackground().setValue(Background.fromColor(Color.RED));
//        Scene s = new Scene(box);
//        guiManager.showScreen(s);
    }

    @Override
    public void render(float partial) {
        startRender();

        // render scene
        for (Scene scene : guiManager.getHuds().values()) {
            renderScene(scene);
        }
        if (guiManager.isDisplayingScreen()) {
            renderScene(guiManager.getDisplayingScreen());
        }

        endRender();
    }

    public TTFontHelper getFontHelper() {
        return fontHelper;
    }

    public void setClipRect(Vector4fc clipRect) {
        ShaderManager.INSTANCE.setUniform("u_ClipRect", clipRect);
    }

    private void startRender() {
        ShaderManager.INSTANCE.bindShader(shader.getValue());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        int width = window.getWidth(), height = window.getHeight();
        ShaderManager.INSTANCE.setUniform("u_ProjMatrix", new Matrix4f().setOrtho(0, width, height, 0, 1000, -1000));
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f());
        ShaderManager.INSTANCE.setUniform("u_WindowSize", new Vector2f(width, height));
        ShaderManager.INSTANCE.setUniform("u_ClipRect", new Vector4f(0, 0, width, height));
    }

    private void endRender() {
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
    }

    private void renderScene(Scene scene) {
        if (window.isResized()) {
            scene.setSize(window.getWidth(), window.getHeight());
        }

        scene.update();

        Container root = scene.getRoot();
        if (!root.visible().get())
            return;
        if(root.closeRequired()){
            guiManager.closeScreen();
            return;
        }

        graphics.pushClipRect(0, 0, scene.width().get(), scene.height().get());
        root.getRenderer().render(root, graphics);
        graphics.popClipRect();
    }

//    private void debug(RenderContext context) {
//        graphics.setColor(Color.WHITE);
//
//        // TODO: CrossHair, move it.
//        int middleX = context.getWindow().getWidth() / 2, middleY = context.getWindow().getHeight() / 2;
//        graphics.drawLine(middleX, middleY - 10, middleX, middleY + 10);
//        graphics.drawLine(middleX - 10, middleY, middleX + 10, middleY);
//    }

    @Override
    public void dispose() {
        ShaderManager.INSTANCE.unregisterShader("gui_shader");
    }
}
