package nullengine.client.rendering.block;

import java.util.Objects;

public class BlockRenderType {

    private final String name;
    private final boolean opaque;

    public BlockRenderType(String name, boolean opaque) {
        this.name = Objects.requireNonNull(name);
        this.opaque = opaque;
    }

    public String getName() {
        return name;
    }

    public boolean isOpaque() {
        return opaque;
    }

    @Override
    public String toString() {
        return "BlockRenderType{" +
                "name='" + name + '\'' +
                ", opaque=" + opaque +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockRenderType that = (BlockRenderType) o;

        if (opaque != that.opaque) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (opaque ? 1 : 0);
        return result;
    }
}
