package engine.graphics.util;

import engine.Platform;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImageWrite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class GLHelper {

    public static ByteBuffer getResourcesAsBuffer(String resource, int size) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = GLHelper.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = BufferUtils.createByteBuffer(size);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = BufferUtils.resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            } catch (Exception e) {
                throw new IOException(String.format("cannot load resource: %s", resource), e);
            }
        }

        buffer.flip();
        return buffer.slice();
    }

    public static void takeScreenshot(Path storagePath){
        var window = Platform.getEngineClient().getGraphicsManager().getWindow();
        var buffer = ByteBuffer.allocateDirect(window.getWidth() * window.getHeight() * 4);
        GL11.glReadPixels(0,0, window.getWidth(),window.getHeight(),GL_RGBA,GL_UNSIGNED_BYTE,buffer);
        var filename = "Screenshot-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) + ".png";
        if(Files.notExists(storagePath)){
            try {
                Files.createDirectory(storagePath);
            } catch (IOException e) {
                Platform.getLogger().warn("Cannot create directory " + storagePath, e);
                return;
            }
        }
        var path = storagePath.resolve(filename).toAbsolutePath().toString();
        STBImageWrite.stbi_flip_vertically_on_write(true);
        if(STBImageWrite.stbi_write_png(path, window.getWidth(), window.getHeight(), 4, buffer, 0)){
            Platform.getLogger().info("Screenshot successfully saved at " + path);
        }
        else {
            Platform.getLogger().warn("Cannot save screenshot at " + path + "!");
        }

    }
}
