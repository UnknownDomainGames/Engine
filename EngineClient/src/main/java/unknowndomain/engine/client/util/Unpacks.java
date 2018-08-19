package unknowndomain.engine.client.util;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Unpacks {
    /**
     * decode png bytes to raw image bytes
     *
     * @param png The png data
     * @return The raw image bytes
     */
    public static ByteBuffer pngToDirect(byte[] png) {
        try {
            PNGDecoder decoder = new PNGDecoder(new ByteArrayInputStream(png));
            ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
