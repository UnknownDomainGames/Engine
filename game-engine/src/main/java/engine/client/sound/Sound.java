package engine.client.sound;

import java.nio.ShortBuffer;

public interface Sound {
    void reload(ShortBuffer buffer, byte bitDepth, int rate, int channel);

    byte getBitDepth();

    int getChannel();

    int getRate();

    void dispose();

    boolean isDisposed();
}
