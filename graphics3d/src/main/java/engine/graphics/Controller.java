package engine.graphics;

@FunctionalInterface
public interface Controller {

    void update(Node3D node, float tpf);
}
