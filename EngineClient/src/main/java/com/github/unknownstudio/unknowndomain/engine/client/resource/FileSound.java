package com.github.unknownstudio.unknowndomain.engine.client.resource;


import com.github.unknownstudio.unknowndomain.engineapi.Platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.*;

public class FileSound extends Sound {

    private final File file;

    private final URI uri;

    public FileSound(File file){
        this.file = file;
        this.uri = file.toURI();
    }

    public FileSound(URI uri){
        this.uri = uri;
        this.file = new File(uri);
    }

    @Override
    public boolean loadSound() {
        MappedByteBuffer buf = null;
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                FileChannel fc = raf.getChannel()){
            buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            buf.load();

            stackPush();
            IntBuffer channelb = stackMallocInt(1);
            stackPush();
            IntBuffer rateb = stackMallocInt(1);

            ShortBuffer raw = stb_vorbis_decode_memory(buf, channelb, rateb);

            int channel = channelb.get();
            int rate = rateb.get();
            stackPop();
            stackPop();
            return loadSound(raw, 16, rate, channel);

        } catch (FileNotFoundException e) {
            Platform.LOGGER.getLogger("Engine Client").warn("Cannot load sound! (file not found) file: {}", file.getAbsolutePath());
            return false;
        } catch (IOException e) {
            Platform.LOGGER.getLogger("Engine Client").warn("Cannot load sound! (IO exception) file: {}", file.getAbsolutePath());
            return false;
        } finally {
            if (buf != null) {
                buf.clear();
            }
        }
    }
}
