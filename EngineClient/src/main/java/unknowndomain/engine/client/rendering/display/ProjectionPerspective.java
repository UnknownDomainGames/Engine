package unknowndomain.engine.client.rendering.display;

import org.joml.Matrix4f;

public class ProjectionPerspective implements Projection {
    private int width, height;

    public ProjectionPerspective(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Matrix4f projection() {
        return new Matrix4f().perspective((float) (Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f)))), width / (float) height, 0.01f, 1000f);
    }
}
