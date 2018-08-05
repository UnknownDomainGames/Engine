package unknowndomain.engine.unclassified;

public class Identifier {
    public final Type type;

    public Identifier(Type type) {
        this.type = type;
    }

    public enum Type {
        ACTION, GAME_OBJECT_TYPE, RESOURCE, GAME_OBJECT
    }
}
