package nullengine.client.rendering.util;

public interface BufferUploadListener {

    /**
     * @return if true, this buffer should no longer be referenced by it's buffer uploader
     * (it mean this buffer may be reused or disposed).
     */
    boolean onUploaded();
}
