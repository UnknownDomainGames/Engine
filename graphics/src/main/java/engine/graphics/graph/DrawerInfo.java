package engine.graphics.graph;

public class DrawerInfo {
    private String shader;
    private DrawDispatcher drawDispatcher;

    public static DrawerInfo drawer() {
        return new DrawerInfo();
    }

    public DrawerInfo shader(String shader) {
        this.shader = shader;
        return this;
    }

    public DrawerInfo drawDispatcher(DrawDispatcher drawDispatcher) {
        this.drawDispatcher = drawDispatcher;
        return this;
    }

    public String getShader() {
        return shader;
    }

    public DrawDispatcher getDrawDispatcher() {
        return drawDispatcher;
    }
}
