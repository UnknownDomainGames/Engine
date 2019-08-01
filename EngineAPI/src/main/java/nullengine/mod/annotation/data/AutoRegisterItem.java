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

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
