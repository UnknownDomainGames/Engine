package engine.client.sound;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.client.asset.*;
import engine.client.asset.exception.AssetLoadException;
import engine.client.asset.exception.AssetNotFoundException;
import engine.client.asset.reloading.AssetReloadHandler;
import engine.client.asset.source.AssetSourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class ALSoundManager implements SoundManager, AssetProvider<Sound> {

    private static final Logger LOGGER = LoggerFactory.getLogger("OpenAL");

    private long device;
    private long context;

    private AssetType<Sound> assetType;
    private AssetSourceManager sourceManager;
    private final Set<Asset<Sound>> audios = new HashSet<>();

    private final Map<String, MutableObjectValue<SoundSource>> audioSourceMap = new HashMap<>();

    private SoundListener listener;

    public static List<String> getDevices() {
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

    public ALSoundManager() {
        init(getSystemDefaultDevice());
    }

    public void init(String device) throws IllegalStateException {
        LOGGER.info("Initialing audio device: " + device);
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
        LOGGER.info("Initialized audio device!");
    }

    @Override
    public SoundSource createSoundSource(String name) {
        SoundSource source = new ALSoundSource(false, false);
        source.createSource();
        audioSourceMap.put(name, new SimpleMutableObjectValue<>(source));
        return source;
    }

    @Override
    public MutableObjectValue<SoundSource> getSoundSource(String name) {
        return audioSourceMap.get(name);
    }

    @Override
    public SoundListener getListener() {
        return listener;
    }

    public static String getSystemDefaultDevice() {
        return alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    }

    public void reload() {
        audios.forEach(asset -> {
            var path = sourceManager.getPath(asset.getUrl().toFileLocation(assetType));
            if (path.isPresent()) {
                try (var fileChannel = Files.newByteChannel(path.get(), StandardOpenOption.READ)) {
                    ByteBuffer buf = BufferUtils.createByteBuffer((int) fileChannel.size());
                    fileChannel.read(buf);
                    buf.flip();
                    try (MemoryStack stack = stackPush()) {
                        IntBuffer channel = stack.mallocInt(1);
                        IntBuffer rate = stack.mallocInt(1);
                        ShortBuffer raw = stb_vorbis_decode_memory(buf, channel, rate);
                        asset.get().reload(raw, (byte) 16, rate.get(0), channel.get(0));
                    }
                } catch (IOException e) {
                    throw new AssetLoadException("Cannot reload audio. Path: " + asset.getUrl().toFileLocation(assetType), e);
                }
            } else {
                throw new AssetNotFoundException(asset.getUrl().toFileLocation(assetType));
            }
        });
    }

    @Override
    public void init(AssetManager manager, AssetType<Sound> type) {
        assetType = type;
        sourceManager = manager.getSourceManager();
        manager.getReloadManager().addHandler(AssetReloadHandler.builder().name("Sound").runnable(this::reload).build());
    }

    @Override
    public void register(Asset<Sound> asset) {
        audios.add(asset);
    }

    @Override
    public void unregister(Asset<Sound> asset) {
        audios.remove(asset);
    }

    @Nonnull
    @Override
    public Sound loadDirect(AssetURL url) throws AssetLoadException, AssetNotFoundException {
        var path = sourceManager.getPath(url.toFileLocation(assetType));
        if (path.isPresent()) {
            try (var channel = Files.newByteChannel(path.get(), StandardOpenOption.READ)) {
                ByteBuffer buf = BufferUtils.createByteBuffer((int) channel.size());
                channel.read(buf);
                buf.flip();
                return ALSound.ofOGG(buf);
            } catch (IOException e) {
                throw new AssetLoadException("Cannot reload audio. Path: " + url.toFileLocation(assetType), e);
            }
        } else {
            throw new AssetNotFoundException(url.toFileLocation(assetType));
        }
    }

    public void dispose() {
        audioSourceMap.values().forEach(source -> source.ifPresent(SoundSource::dispose));
        audios.forEach(Asset::dispose);
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

    public boolean isDisposed() {
        return context == 0;
    }
}
