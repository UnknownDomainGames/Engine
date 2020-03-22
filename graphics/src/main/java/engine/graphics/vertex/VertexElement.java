package engine.graphics.vertex;

import engine.graphics.util.DataType;
import engine.math.Math2;

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
    private final int indexCount;
    private final int divisor;
    private final boolean normalized;
    private final int hash;

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount) {
        this(type, name, componentCount, 0, false);
    }

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount, boolean normalized) {
        this(type, name, componentCount, 0, normalized);
    }

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount, int divisor) {
        this(type, name, componentCount, divisor, false);
    }

    public VertexElement(@Nonnull DataType type, @Nonnull String name, int componentCount, int divisor, boolean normalized) {
        this.type = notNull(type);
        this.name = notNull(name);
        this.componentCount = componentCount;
        this.bytes = componentCount * type.getBytes();
        this.indexCount = Math2.ceilDiv(componentCount, 4);
        this.divisor = divisor;
        this.normalized = normalized;
        this.hash = Objects.hash(type, name, componentCount, divisor, normalized);
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

    public int getIndexCount() {
        return indexCount;
    }

    public int getDivisor() {
        return divisor;
    }

    public boolean isNormalized() {
        return normalized;
    }

    @Override
    public String toString() {
        return "VertexElement{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", componentCount=" + componentCount +
                ", divisor=" + divisor +
                ", normalized=" + normalized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexElement that = (VertexElement) o;
        return componentCount == that.componentCount &&
                normalized == that.normalized &&
                type == that.type &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
