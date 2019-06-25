package nullengine.client.sound;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import nullengine.Platform;
import nullengine.client.asset.AssetPath;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.rendering.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.lwjgl.openal.ALC10.*;

public class EngineSoundManager implements ALSoundManager {

    private long device;

    private long context;

    private ALSoundListener listener;

    private final Map<AssetPath, MutableValue<ALSound>> soundMap = new HashMap<>();

    private final Map<String, MutableValue<ALSoundSource>> soundSourceMap = new HashMap<>();

    private Matrix4fc cameraMatrix;

    public EngineSoundManager() {
        cameraMatrix = new Matrix4f();
    }

    public void init() {
        init(getSystemDefaultDevice());
    }

    @Override
    public void init(String device) throws IllegalStateException {
        this.device = alcOpenDevice(device);
        if (this.device == 0L) {
            throw new IllegalStateException(String.format("Fail to open OpenAL device \"%s\"", device));
        }
        ALCCapabilities cap = ALC.createCapabilities(this.device);
        this.context = alcCreateContext(this.device, (IntBuffer) null);
        if (context == 0L) {
            throw new IllegalStateException(String.format("Fail to create OpenAL context for device \"%s\"", device));
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(cap);
        listener = new ALSoundListener();
    }

    @Override
    public void updateListener(Camera camera) {
        cameraMatrix = camera.getViewMatrix();
        // Optimized version to get lookAt vector and Up vector from View Matrix, provided by author of JOML at a LWJGL forum
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.position((Vector3f) camera.getPosition()).orient(at, up);
    }

    @Override
    public ALSoundSource createSoundSource(String name) {
        ALSoundSource source = new ALSoundSource(false, false);
        source.createSource();
        soundSourceMap.put(name, new SimpleMutableObjectValue<>(source));
        return source;
    }

    @Override
    public MutableValue<ALSoundSource> getSoundSource(String name) {
        return soundSourceMap.get(name);
    }

    @Override
    public MutableValue<ALSound> getSound(AssetPath path) {
        return soundMap.computeIfAbsent(path, key -> new SimpleMutableObjectValue<>(getSoundDirect(key)));

    }

    @Override
    public ALSound getSoundDirect(AssetPath path) {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(path);
        if (nativePath.isPresent()) {
            try (var channel = Files.newByteChannel(nativePath.get(), StandardOpenOption.READ)) {
                ByteBuffer buf = BufferUtils.createByteBuffer((int) channel.size());
                channel.read(buf);
                buf.flip();
                return ALSound.ofOGG(buf);
            } catch (IOException e) {
                throw new AssetLoadException("Cannot load sound. Path: " + path, e);
            }
        } else {
            throw new AssetLoadException("Cannot load sound (Missing file). Path: " + path);
        }
    }

    @Override
    public ALSoundListener getListener() {
        return listener;
    }

    public static List<String> listDevices() {
        if (alcIsExtensionPresent(0, "ALC_ENUMERATION_EXT")) {
            String a = alcGetString(0, ALC_DEVICE_SPECIFIER);
            if (a != null) {
                a = a.replace('\0', '\n');
                return Arrays.asList(a.split("\n"));
            }
            return new ArrayList<>();
        }
        throw new UnsupportedOperationException("Enumeration of devices is not supported");
    }

    public static String getSystemDefaultDevice() {
        return alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    }

    public void reload() {
        for (Map.Entry<AssetPath, MutableValue<ALSound>> value : soundMap.entrySet()) {
            Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(value.getKey());
            if (nativePath.isPresent()) {
                try (var channel = Files.newByteChannel(nativePath.get(), StandardOpenOption.READ)) {
                    ByteBuffer buf = BufferUtils.createByteBuffer((int) channel.size());
                    channel.read(buf);
                    buf.flip();
                    value.getValue().getValue().reloadOgg(buf);
                } catch (IOException e) {
                    throw new AssetLoadException("Cannot reload sound. Path: " + value.getKey(), e);
                }
            } else {
                Platform.getLogger().warn(String.format("Cannot find source of sound %s when reloading! Attempt to keep it", value.getKey()));
            }
        }
    }

    @Override
    public void dispose() {
        soundSourceMap.forEach((k, v) -> v.ifPresent(ALSoundSource::dispose));
        soundMap.forEach((k, v) -> v.ifPresent(ALSound::dispose));
        listener = null;
        if (context != 0) {
            alcDestroyContext(context);
            context = 0;
        }
        if (device != 0) {
            alcCloseDevice(device);
            device = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return context == 0;
    }
}
