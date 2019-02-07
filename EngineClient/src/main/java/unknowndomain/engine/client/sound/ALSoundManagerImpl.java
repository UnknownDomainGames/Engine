package unknowndomain.engine.client.sound;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.rendering.camera.Camera;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.openal.ALC10.*;

public class ALSoundManagerImpl implements ALSoundManager {

    private long device;

    private long context;

    private ALSoundListener listener;

    private final Cache<AssetPath, ALSound> soundMap = CacheBuilder.newBuilder().weakValues().removalListener(notification -> ((ALSound) notification.getValue()).dispose()).build();

    private final Cache<String, ALSoundSource> soundSourceMap = CacheBuilder.newBuilder().weakValues().removalListener(notification -> ((ALSoundSource) notification.getValue()).dispose()).build();

    private Matrix4f cameraMatrix;

    public ALSoundManagerImpl(){
        cameraMatrix = new Matrix4f();
    }

    public void init(){
        init(getSystemDefaultDevice());
    }

    @Override
    public void init(String device) throws IllegalStateException{
        this.device = alcOpenDevice(device);
        if(this.device == 0L){
            throw new IllegalStateException(String.format("Fail to open OpenAL device \"%s\"", device));
        }
        ALCCapabilities cap = ALC.createCapabilities(this.device);
        this.context = alcCreateContext(this.device, (IntBuffer)null);
        if(context == 0L){
            throw new IllegalStateException(String.format("Fail to create OpenAL context for device \"%s\"", device));
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(cap);
        listener = new ALSoundListener();
    }

    @Override
    public void updateListener(Camera camera){
        cameraMatrix = camera.view(0);
        // Optimized version to get lookAt vector and Up vector from View Matrix, provided by author of JOML at a LWJGL forum
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.orient(at,up);
    }

    @Override
    public ALSoundSource createSoundSource(String name){
        ALSoundSource source = new ALSoundSource(false, false);
        return source;
    }

    @Override
    public ALSoundSource getSoundSource(String name) {
        try {
            return soundSourceMap.get(name, ()->createSoundSource(name));
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public ALSound getSound(AssetPath path, boolean reload){
        if(!reload){
            var sound = soundMap.getIfPresent(path);
            if(sound != null) {
                return sound;
            }
        }
        try {
            return soundMap.get(path, ()->{
                Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getPath(path);
                if (nativePath.isPresent()) {
                    try (var channel = Files.newByteChannel(nativePath.get(), StandardOpenOption.READ)) {
                        ByteBuffer buf = BufferUtils.createByteBuffer((int) channel.size());
                        channel.read(buf);
                        return ALSound.ofOGG(buf);
                    }
                }
                throw new AssetLoadException("Cannot load sound. Path: " + path);
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public ALSoundListener getListener() {
        return listener;
    }

    public static List<String> listDevices(){
        if(alcIsExtensionPresent(0, "ALC_ENUMERATION_EXT")){
            String a = alcGetString(0, ALC_DEVICE_SPECIFIER);
            if (a != null) {
                a = a.replace('\0', '\n');
                return Arrays.asList(a.split("\n"));
            }
            return new ArrayList<>();
        }
        throw new UnsupportedOperationException("Enumeration of devices is not supported");
    }

    public static String getSystemDefaultDevice(){
        return alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    }

    @Override
    public void dispose() {
        soundSourceMap.cleanUp();
        soundMap.cleanUp();
        listener = null;
        if(context != 0){
            alcDestroyContext(context);
            context = 0;
        }
        if(device != 0){
            alcCloseDevice(device);
            device = 0;
        }
    }
}
