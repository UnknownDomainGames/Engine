package engine.graphics.graph;

public class DepthOutputInfo {
    private String name;
    private boolean clear = false;
    private float clearValue = 0f;

    public static DepthOutputInfo depthOutput() {
        return new DepthOutputInfo();
    }

    public DepthOutputInfo name(String name) {
        this.name = name;
        return this;
    }

    public DepthOutputInfo clear() {
        this.clear = true;
        return this;
    }

    public DepthOutputInfo clearValue(float value) {
        this.clearValue = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isClear() {
        return clear;
    }

    public float getClearValue() {
        return clearValue;
    }
}
