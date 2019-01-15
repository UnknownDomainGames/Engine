package unknowndomain.engine.client.rendering.block;

public class BlockRenderType {

    private final String name;
    private final Transparency transparency;

    public BlockRenderType(String name, Transparency transparency) {
        this.name = name;
        this.transparency = transparency;
    }

    public String getName() {
        return name;
    }

    public Transparency getTransparency() {
        return transparency;
    }

    @Override
    public String toString() {
        return "BlockRenderType{" +
                "name='" + name + '\'' +
                ", transparency=" + transparency +
                '}';
    }
}
