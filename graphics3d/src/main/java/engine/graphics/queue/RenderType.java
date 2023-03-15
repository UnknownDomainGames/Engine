package engine.graphics.queue;

import static engine.util.Validate.notNull;

public final class RenderType {

    public static final RenderType SKY = create("Sky");
    public static final RenderType GUI = create("Gui");
    public static final RenderType OPAQUE = create("Opaque");
    public static final RenderType TRANSPARENT = create("Transparent");
    public static final RenderType TRANSLUCENT = create("Translucent");
    public static final RenderType WATER = create("Water");
    public static final RenderType LIQUID_OPAQUE = create("LiquidOpaque");
    public static final RenderType LIQUID_TRANSLUCENT = create("LiquidTranslucent");

    private final String name;

    public static RenderType create(String name) {
        return new RenderType(name);
    }

    private RenderType(String name) {
        this.name = notNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderType that = (RenderType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "RenderType{" +
                "name='" + name + '\'' +
                '}';
    }
}
