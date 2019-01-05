package unknowndomain.engine.client.rendering.world.chunk;

public class ChunkRenderLayer {

    private final String name;

    private ChunkRenderLayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ChunkRenderLayer{" +
                "name='" + name + '\'' +
                '}';
    }
}
