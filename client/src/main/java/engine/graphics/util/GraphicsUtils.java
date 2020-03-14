package engine.graphics.util;

import engine.Platform;
import engine.graphics.GraphicsManager;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FrameBuffer;
import org.lwjgl.stb.STBImageWrite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GraphicsUtils {

    public static void takeScreenshot(Path storagePath) {
        GraphicsManager graphicsManager = Platform.getEngineClient().getGraphicsManager();
        FrameBuffer frameBuffer = graphicsManager.getRenderGraph().getOutputFrameBuffer();
        var buffer = ByteBuffer.allocateDirect(frameBuffer.getWidth() * frameBuffer.getHeight() * 4);
        frameBuffer.readPixels(ColorFormat.RGBA8, buffer);
        var filename = "Screenshot-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) + ".png";
        if (Files.notExists(storagePath)) {
            try {
                Files.createDirectory(storagePath);
            } catch (IOException e) {
                Platform.getLogger().warn("Cannot create directory " + storagePath, e);
                return;
            }
        }
        var path = storagePath.resolve(filename).toAbsolutePath().toString();
        STBImageWrite.stbi_flip_vertically_on_write(true);
        if (STBImageWrite.stbi_write_png(path, frameBuffer.getWidth(), frameBuffer.getHeight(), 4, buffer, 0)) {
            Platform.getLogger().info("Screenshot successfully saved at " + path);
        } else {
            Platform.getLogger().warn("Cannot save screenshot at " + path + "!");
        }
    }

    private GraphicsUtils() {
    }
}
