package nullengine.client.rendering.vertex;

import nullengine.client.rendering.util.DataType;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class VertexElement {

    public static final String NAME_POSITION = "Position";
    public static final String NAME_COLOR = "Color";
    public static final String NAME_TEX_COORD = "TexCoord";
    public static final String NAME_NORMAL = "Normal";
    public static final String NAME_TANGENT = "Tangent";
    public static final String NAME_BITANGENT = "Bitangent";
    public static final String NAME_UNKNOWN = "Unknown";

    public static final VertexElement POSITION = new VertexElement(DataType.FLOAT, NAME_POSITION, 3);
    public static final VertexElement COLOR_RGB = new VertexElement(DataType.FLOAT, NAME_COLOR, 3);
    public static final VertexElement COLOR_RGBA = new VertexElement(DataType.FLOAT, NAME_COLOR, 4);
    public static final VertexElement TEX_COORD = new VertexElement(DataType.FLOAT, NAME_TEX_COORD, 2);
    public static final VertexElement NORMAL = new VertexElement(DataType.FLOAT, NAME_NORMAL, 3, true);
    public static final VertexElement TANGENT = new VertexElement(DataType.FLOAT, NAME_TANGENT, 3, true);
    public static final VertexElement BITANGENT = new VertexElement(DataType.FLOAT, NAME_BITANGENT, 3, true);

    private final DataType type;
    private final String name;
    private final int componentCount;
    private final int bytes;
    private final boolean normalized;

    public VertexElement(@Nonnull DataType type, int componentCount) {
        this(type, NAME_UNKNOWN, componentCount, false);
    }

    public VertexElement(@Nonnull DataType type, int componentCount, boolean normalized) {
        this(type, NAME_UNKNOWN, componentCount, normalized);
    }

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount) {
        this(type, name, componentCount, false);
    }

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount, boolean normalized) {
        this.type = notNull(type);
        this.name = notNull(name);
        this.componentCount = componentCount;
        this.bytes = componentCount * type.getBytes();
        this.normalized = normalized;
    }

    public DataType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public int getBytes() {
        return bytes;
    }

    public boolean isNormalized() {
        return normalized;
    }

    @Override
    public String toString() {
        return "VertexElement{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", size=" + componentCount +
                ", normalized=" + normalized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexElement that = (VertexElement) o;
        return componentCount == that.componentCount &&
                bytes == that.bytes &&
                normalized == that.normalized &&
                type == that.type &&
                name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, componentCount, bytes, normalized);
    }
}
