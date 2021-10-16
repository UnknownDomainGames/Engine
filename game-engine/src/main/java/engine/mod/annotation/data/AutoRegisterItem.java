package engine.mod.annotation.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AutoRegisterItem {

    public enum Kind {
        CLASS,
        FIELD
    }

    private final Kind kind;
    private final String owner;
    private final @Nullable
    String type;
    private final @Nullable
    String field;

    private AutoRegisterItem(Kind kind, String owner, @Nullable String type, @Nullable String field) {
        this.kind = kind;
        this.owner = owner;
        this.type = type;
        this.field = field;
    }

    public AutoRegisterItem(String owner, @Nonnull String type, @Nonnull String field) {
        this(Kind.FIELD, owner, type, field);
    }

    public AutoRegisterItem(String owner) {
        this(Kind.CLASS, owner, null, null);
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
