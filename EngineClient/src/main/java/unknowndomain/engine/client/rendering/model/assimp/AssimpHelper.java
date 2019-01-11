package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.assimp.*;
import unknowndomain.engine.client.rendering.util.GLHelper;
import unknowndomain.engine.client.resource.ResourcePath;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class AssimpHelper {
    public static final AIFileIO ASSIMP_JARFILEIO;

    static {
        ASSIMP_JARFILEIO = AIFileIO.create();
        var openproc = new AIFileOpenProc(){
            @Override
            public long invoke(long pFileIO, long fileName, long openMode) {
                AIFile aiFile = AIFile.create();
                final ByteBuffer data;
                String fileNameUtf8 = memUTF8(fileName);
                try {
                    data = GLHelper.getResourcesAsBuffer(fileNameUtf8, 8192);
                } catch (IOException e) {
                    throw new RuntimeException("Could not open file: " + fileNameUtf8);
                }
                AIFileReadProcI fileReadProc = new AIFileReadProc() {
                    public long invoke(long pFile, long pBuffer, long size, long count) {
                        long max = Math.min(data.remaining(), size * count);
                        memCopy(memAddress(data) + data.position(), pBuffer, max);
                        return max;
                    }
                };
                AIFileSeekI fileSeekProc = new AIFileSeek() {
                    public int invoke(long pFile, long offset, int origin) {
                        if (origin == Assimp.aiOrigin_CUR) {
                            data.position(data.position() + (int) offset);
                        } else if (origin == Assimp.aiOrigin_SET) {
                            data.position((int) offset);
                        } else if (origin == Assimp.aiOrigin_END) {
                            data.position(data.limit() + (int) offset);
                        }
                        return 0;
                    }
                };
                AIFileTellProcI fileTellProc = new AIFileTellProc() {
                    public long invoke(long pFile) {
                        return data.limit();
                    }
                };
                aiFile.ReadProc(fileReadProc);
                aiFile.SeekProc(fileSeekProc);
                aiFile.FileSizeProc(fileTellProc);
                return aiFile.address();
            }
        };
        AIFileCloseProcI closeproc = new AIFileCloseProc() {
            public void invoke(long pFileIO, long pFile) {
                /* Nothing to do */
            }
        };
        ASSIMP_JARFILEIO.set(openproc, closeproc, NULL);
    }

    /**
     * Load a model from jar
     * @param path relative path of the model from root
     * @return a model
     * @todo Use ResourceLocation-styled instead
     */
    public static AssimpModel loadModel(String path){
        AIScene scene = aiImportFileEx(path,
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, ASSIMP_JARFILEIO);
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }
        return new AssimpModel(scene);
    }
}
