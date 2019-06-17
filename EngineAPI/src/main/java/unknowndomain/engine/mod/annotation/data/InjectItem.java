package unknowndomain.engine.mod.annotation.data;

public class InjectItem {

    private String owner;
    private String type;
    private String name;

    public InjectItem(String owner, String type, String name) {
        this.owner = owner;
        this.type = type;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
