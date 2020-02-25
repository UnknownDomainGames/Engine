package engine.graphics.graph;

public class DepthOutputInfo {
    private boolean enable = true;
    private String depthBuffer;
    private boolean clear = false;
    private float clearValue = 0f;
    private boolean writable = true;

    public static DepthOutputInfo depthOutput() {
        return new DepthOutputInfo();
    }

    public boolean isEnable() {
        return enable;
    }

    public DepthOutputInfo setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getDepthBuffer() {
        return depthBuffer;
    }

    public DepthOutputInfo setDepthBuffer(String depthBuffer) {
        this.depthBuffer = depthBuffer;
        return this;
    }

    public boolean isClear() {
        return clear;
    }

    public DepthOutputInfo setClear(boolean clear) {
        this.clear = clear;
        return this;
    }

    public float getClearValue() {
        return clearValue;
    }

    public DepthOutputInfo setClearValue(float clearValue) {
        this.clearValue = clearValue;
        return this;
    }

    public boolean isWritable() {
        return writable;
    }

    public DepthOutputInfo setWritable(boolean writable) {
        this.writable = writable;
        return this;
    }
}
