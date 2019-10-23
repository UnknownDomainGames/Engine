package nullengine.client.sound;

import nullengine.Platform;
import nullengine.util.Disposable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.*;

public class ALSound implements Disposable {

    private int soundId = 0;
    private int channel;
    private int rate;
    private byte bitDepth;

    public ALSound(int soundId, int channel, int rate, byte bitDepth) {
        this.soundId = soundId;
        this.channel = channel;
        this.rate = rate;
        this.bitDepth = bitDepth;
    }

    public static ALSound ofOGG(ByteBuffer buffer) {
        stackPush();
        IntBuffer channelb = stackMallocInt(1);
        stackPush();
        IntBuffer rateb = stackMallocInt(1);

        ShortBuffer raw = stb_vorbis_decode_memory(buffer, channelb, rateb);
        int channel = channelb.get(0);
        int rate = rateb.get(0);
        stackPop();
        stackPop();

        return of(raw, (byte) 16, rate, channel);
    }

    public static ALSound of(ShortBuffer buffer, byte bitDepth, int rate, int channel) {
        alGetError();
        int soundId = alGenBuffers();
        ALSound sound = new ALSound(soundId, channel, rate, bitDepth);
        sound.reload(buffer, bitDepth, rate, channel);
        return sound;
    }

    public void reloadOgg(ByteBuffer buffer) {
        stackPush();
        IntBuffer channelb = stackMallocInt(1);
        stackPush();
        IntBuffer rateb = stackMallocInt(1);

        ShortBuffer raw = stb_vorbis_decode_memory(buffer, channelb, rateb);
        int channel = channelb.get(0);
        int rate = rateb.get(0);
        stackPop();
        stackPop();
        reload(raw, (byte) 16, rate, channel);
    }

    public void reload(ShortBuffer buffer, byte bitDepth, int rate, int channel) {
        int format = 0;
        if (bitDepth == 8) {
            if (channel == 1) format = AL_FORMAT_MONO8;
            else if (channel == 2) format = AL_FORMAT_STEREO8;
        } else if (bitDepth == 16) {
            if (channel == 1) format = AL_FORMAT_MONO16;
            else if (channel == 2) format = AL_FORMAT_STEREO16;
        }
        alBufferData(soundId, format, buffer, rate);
        int err = alGetError();
        switch (err) {
            case AL_NO_ERROR:
                break;
            case AL_INVALID_ENUM:
                Platform.getLogger().warn("Cannot load sound! (Invalid enum) bit depth: {} channel: {}", bitDepth, channel);
                dispose();
            case AL_OUT_OF_MEMORY:
                Platform.getLogger().warn("Cannot load sound! (Out of memory)!");
                dispose();
        }
    }

    public int getSoundId() {
        return soundId;
    }

    public byte getBitDepth() {
        return bitDepth;
    }

    public int getChannel() {
        return channel;
    }

    public int getRate() {
        return rate;
    }

    public void dispose() {
        if (soundId != 0) {
            alDeleteBuffers(soundId);
            soundId = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return soundId == 0;
    }
}
