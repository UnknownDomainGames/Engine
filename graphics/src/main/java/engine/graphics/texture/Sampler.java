package engine.graphics.texture;

public interface Sampler {
    int getId();

    void setMagFilter(FilterMode filterMode);

    void setMinFilter(FilterMode filterMode);

    void setWrapS(WrapMode wrapMode);

    void setWrapT(WrapMode wrapMode);

    void setWrapR(WrapMode wrapMode);

    void dispose();

    boolean isDisposed();
}
