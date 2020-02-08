package nullengine.client.rendering3d;

@FunctionalInterface
public interface Controller {

    void update(Node3D node, float tpf);
}
