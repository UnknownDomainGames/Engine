package engine.graphics.vertex;

import engine.graphics.util.DataType;

import java.util.Arrays;

import static engine.graphics.vertex.VertexElement.*;

public final class VertexFormat {

    public static final VertexFormat NONE = of();
    public static final VertexFormat POSITION = of(VertexElement.POSITION);
    public static final VertexFormat COLOR = of(VertexElement.COLOR_RGB);
    public static final VertexFormat COLOR_ALPHA = of(VertexElement.COLOR_RGBA);
    public static final VertexFormat TEX_COORD = of(VertexElement.TEX_COORD);
    public static final VertexFormat NORMAL = of(VertexElement.NORMAL);
    public static final VertexFormat TANGENT = of(VertexElement.TANGENT);
    public static final VertexFormat BITANGENT = of(VertexElement.BITANGENT);

    public static final VertexFormat POSITION_COLOR = of(POSITION, VertexElement.COLOR_RGB);
    public static final VertexFormat POSITION_COLOR_ALPHA = of(POSITION, VertexElement.COLOR_RGBA);
    public static final VertexFormat POSITION_COLOR_TEX_COORD =
            of(POSITION_COLOR, VertexElement.TEX_COORD);
    public static final VertexFormat POSITION_COLOR_ALPHA_TEX_COORD =
            of(POSITION_COLOR_ALPHA, VertexElement.TEX_COORD);
    public static final VertexFormat POSITION_TEX_COORD =
            of(POSITION, VertexElement.TEX_COORD);
    public static final VertexFormat POSITION_TEX_COORD_NORMAL =
            of(POSITION_TEX_COORD, VertexElement.NORMAL);
    public static final VertexFormat POSITION_COLOR_TEX_COORD_NORMAL =
            of(POSITION_COLOR_TEX_COORD, VertexElement.NORMAL);
    public static final VertexFormat POSITION_COLOR_ALPHA_TEX_COORD_NORMAL =
            of(POSITION_COLOR_ALPHA_TEX_COORD, VertexElement.NORMAL);

    private final VertexElement[] elements;
    private final Entry[] entries;
    private final int bytes;
    private final int hash;
    private final boolean instanced;

    private int positionElement = -1;
    private int colorElement = -1;
    private int texCoordElement = -1;
    private int normalElement = -1;
    private int tangentElement = -1;
    private int bitangentElement = -1;

    private boolean usingAlpha;

    public static VertexFormat of(VertexElement... elements) {
        return new VertexFormat(elements);
    }

    public static VertexFormat of(VertexFormat parent, VertexElement element) {
        int parentElementsLength = parent.elements.length;
        VertexElement[] newElements = Arrays.copyOf(parent.elements, parentElementsLength + 1);
        newElements[parentElementsLength] = element;
        return new VertexFormat(newElements);
    }

    public static VertexFormat of(VertexFormat parent, VertexElement... elements) {
        int parentElementsLength = parent.elements.length;
        VertexElement[] newElements = new VertexElement[parentElementsLength + elements.length];
        System.arraycopy(parent.elements, 0, newElements, 0, parentElementsLength);
        System.arraycopy(elements, 0, newElements, parentElementsLength, elements.length);
        return new VertexFormat(newElements);
    }

    public VertexFormat(VertexElement... elements) {
        this.elements = elements;
        int index = 0;
        int bytes = 0;
        int hash = 0;
        boolean instanced = false;
        for (int i = 0; i < elements.length; i++) {
            VertexElement element = elements[i];
            index += element.getIndexCount();
            bytes += element.getBytes();
            hash = hash * 31 + element.hashCode();
            instanced |= element.isInstanced();
            switch (element.getName()) {
                case NAME_POSITION:
                    positionElement = i;
                    break;
                case NAME_COLOR:
                    colorElement = i;
                    usingAlpha |= element.getComponentCount() == 4;
                    break;
                case NAME_TEX_COORD:
                    texCoordElement = i;
                    break;
                case NAME_NORMAL:
                    normalElement = i;
                    break;
                case NAME_TANGENT:
                    tangentElement = i;
                    break;
                case NAME_BITANGENT:
                    bitangentElement = i;
                    break;
            }
        }
        this.entries = initEntries(elements, index);
        this.bytes = bytes;
        this.hash = hash;
        this.instanced = instanced;
    }

    private Entry[] initEntries(VertexElement[] elements, int count) {
        Entry[] entries = new Entry[count];
        int index = 0;
        int offset = 0;
        for (VertexElement element : elements) {
            for (int i = 0, size = element.getIndexCount(); i < size; i++, index++) {
                entries[index] = new Entry(index, element, offset + i * 16);
            }
            offset += element.getBytes();
        }
        return entries;
    }

    public VertexElement[] getElements() {
        return elements;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public int getIndexCount() {
        return entries.length;
    }

    public int getBytes() {
        return bytes;
    }

    public boolean isInstanced() {
        return instanced;
    }

    public boolean isUsingPosition() {
        return positionElement != -1;
    }

    public boolean isUsingColor() {
        return colorElement != -1;
    }

    public boolean isUsingAlpha() {
        return usingAlpha;
    }

    public boolean isUsingTexCoord() {
        return texCoordElement != -1;
    }

    public boolean isUsingNormal() {
        return normalElement != -1;
    }

    public boolean isUsingTangent() {
        return tangentElement != -1;
    }

    public boolean isUsingBitangent() {
        return bitangentElement != -1;
    }

    @Override
    public String toString() {
        return "VertexFormat{" +
                "elements=" + Arrays.toString(elements) +
                ", bytes=" + bytes +
                ", instanced=" + instanced +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexFormat that = (VertexFormat) o;
        return Arrays.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static final class Entry {
        private final int index;
        private final VertexElement element;
        private final int offset;

        public Entry(int index, VertexElement element, int offset) {
            this.index = index;
            this.element = element;
            this.offset = offset;
        }

        public int getIndex() {
            return index;
        }

        public VertexElement getElement() {
            return element;
        }

        public int getSize() {
            return element.getComponentCount();
        }

        public DataType getType() {
            return element.getType();
        }

        public boolean isNormalized() {
            return element.isNormalized();
        }

        public int getOffset() {
            return offset;
        }

        public int getDivisor() {
            return element.getDivisor();
        }
    }
}
