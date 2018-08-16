package unknowndomain.engine.mod.java.harvester;

import org.objectweb.asm.Type;

public class EnumWrapper {

    private final Type type;
    private final String name;

    public EnumWrapper(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Enum getValue() throws ClassNotFoundException {
        return Enum.valueOf((Class<Enum>)Class.forName(type.getClassName()), name);
    }
}
