package engine.client.sound;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class ALSound implements Sound {

    private int id;
    private int channel;
    private int rate;
    private byte bitDepth;

    public ALSound(int id, int channel, int rate, byte bitDepth) {
        this.id = id;
        this.channel = channel;
        this.rate = rate;
        this.bitDepth = bitDepth;
    }

    public static Sound ofOGG(ByteBuffer buffer) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer channel = stack.mallocInt(1);
            IntBuffer rate = stack.mallocInt(1);
            ShortBuffer raw = stb_vorbis_decode_memory(buffer, channel, rate);
            return of(raw, (byte) 16, rate.get(0), channel.get(0));
        }
    }

    public static Sound of(ShortBuffer buffer, byte bitDepth, int rate, int channel) {
        int id = alGenBuffers();
        Sound sound = new ALSound(id, channel, rate, bitDepth);
        sound.reload(buffer, bitDepth, rate, channel);
        return sound;
    }

    @Override
    public void reload(ShortBuffer buffer, byte bitDepth, int rate, int channel) {
        int format = 0;
        if (bitDepth == 8) {
            if (channel == 1) format = AL_FORMAT_MONO8;
            else if (channel == 2) format = AL_FORMAT_STEREO8;
        } else if (bitDepth == 16) {
            if (channel == 1) format = AL_FORMAT_MONO16;
            else if (channel == 2) format = AL_FORMAT_STEREO16;
        }
        alBufferData(id, format, buffer, rate);
        switch (alGetError()) {
            case AL_NO_ERROR:
                break;
            case AL_INVALID_ENUM:
                throw new IllegalArgumentException(
                        String.format("Cannot load audio! (Invalid enum) bit depth: %s channel: %s", bitDepth, channel));
            case AL_OUT_OF_MEMORY:
                throw new IllegalStateException("Cannot load audio! (Out of memory)!");
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public byte getBitDepth() {
        return bitDepth;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public int getRate() {
        return rate;
    }

    @Override
    public void dispose() {
        if (id != 0) {
            alDeleteBuffers(id);
            id = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
