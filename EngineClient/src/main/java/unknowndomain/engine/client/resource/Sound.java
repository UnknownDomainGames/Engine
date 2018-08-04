package unknowndomain.engine.client.resource;

import java.nio.ShortBuffer;

import unknowndomain.engine.Platform;
import unknowndomain.engine.registry.RegistryEntry;

import static org.lwjgl.openal.AL10.*;

public class Sound extends RegistryEntry.Impl<Sound> {

    private byte channels;
    private int rate;
    private byte bitDepth;
    private ShortBuffer buffer;

    private int soundId = -1;

    public Sound(){}

    public Sound(ShortBuffer buf, byte bitDepth, int samplingRate, byte channels){
        buffer = buf;
        this.bitDepth = bitDepth;
        this.rate = samplingRate;
        this.channels = channels;
    }

    public boolean loadSound(){
        if(buffer != null){
            return true;
        }
        return false;
    }

    protected boolean loadSound(ShortBuffer raw, int bitDepth, int rate, int channel){
        if(soundId != -1)
            unloadSound();
        alGetError();
        soundId = alGenBuffers();
        int format = 0;
        if(bitDepth == 8){
            if(channels == 1) format = AL_FORMAT_MONO8;
            else if(channels == 2) format = AL_FORMAT_STEREO8;
        }
        else if(bitDepth == 16){
            if(channels == 1) format = AL_FORMAT_MONO16;
            else if(channels == 2) format = AL_FORMAT_STEREO16;
        }
        alBufferData(soundId, format, buffer, rate);
        int err = alGetError();
        switch (err){
            case AL_NO_ERROR:
                break;
            case AL_INVALID_ENUM:
                Platform.getLogger().warn("Cannot load sound! (Invalid enum) bit depth: {} channels: {}", bitDepth, channels);
                return false;
            case AL_OUT_OF_MEMORY:
                Platform.getLogger().warn("Cannot load sound! (Out of memory)!");
                return false;
        }
        return true;
    }

    public int getSoundId(){
        return soundId;
    }

    public void unloadSound(){
        if(soundId != -1){
            alDeleteBuffers(soundId);
            soundId = -1;
        }
    }
}
