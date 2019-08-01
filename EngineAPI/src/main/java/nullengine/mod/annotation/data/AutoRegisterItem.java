package nullengine.mod.annotation.data;

public class AutoRegisterItem {

    public enum Kind {
        CLASS,
        FIELD
    }

    private Kind kind;
    private String owner;
    private String type;
    private String field;

    public AutoRegisterItem(String owner, String type, String field) {
        this.kind = Kind.FIELD;
        this.owner = owner;
        this.type = type;
        this.field = field;
    }

    public AutoRegisterItem(String owner) {
        this.kind = Kind.CLASS;
        this.owner = owner;
    }

    public Kind getKind() {
        return kind;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public String getField() {
        return field;
    }
}
