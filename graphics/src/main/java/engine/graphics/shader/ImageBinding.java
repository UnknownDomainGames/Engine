package engine.graphics.shader;

import engine.graphics.texture.Texture;

public interface ImageBinding {
    int getUnit();

    Texture getTexture();

    void set(Texture texture);

    boolean canRead();

    boolean canWrite();

    void setCanRead(boolean flag);

    void setCanWrite(boolean flag);

}
