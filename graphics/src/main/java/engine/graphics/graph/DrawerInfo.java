package engine.graphics.graph;

public class DrawerInfo {
    private String shader;
    private DrawDispatcher drawDispatcher;

    public static DrawerInfo drawer() {
        return new DrawerInfo();
    }

    public String getShader() {
        return shader;
    }

    public DrawerInfo setShader(String shader) {
        this.shader = shader;
        return this;
    }

    public DrawDispatcher getDrawDispatcher() {
        return drawDispatcher;
    }

    public DrawerInfo setDrawDispatcher(DrawDispatcher drawDispatcher) {
        this.drawDispatcher = drawDispatcher;
        return this;
    }
}
