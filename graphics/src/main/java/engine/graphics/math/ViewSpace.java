package engine.graphics.math;

import org.joml.Vector2f;

public class ViewSpace {
    private Vector2f origin;

    private Vector2f size;

    private Vector2f depthRange;

    public ViewSpace(){
        this(new Vector2f(), new Vector2f(), new Vector2f());
    }

    public ViewSpace(Vector2f origin, Vector2f size, Vector2f depthRange){
        this.origin = origin;
        this.size = size;
        this.depthRange = depthRange;
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public ViewSpace setOrigin(Vector2f origin) {
        this.origin = origin;
        return this;
    }

    public Vector2f getSize() {
        return size;
    }

    public ViewSpace setSize(Vector2f size) {
        this.size = size;
        return this;
    }

    public Vector2f getDepthRange() {
        return depthRange;
    }

    public ViewSpace setDepthRange(Vector2f depthRange) {
        this.depthRange = depthRange;
        return this;
    }
}
