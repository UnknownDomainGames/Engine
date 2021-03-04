package engine.world.chunk;

public class ChunkStatus {
    /**
     * state this chunk is just created
     */
    public static final ChunkStatus EMPTY = new Builder().name("empty").build();
    /**
     * state this chunk is ready for gameplay
     */
    public static final ChunkStatus GAMEPLAY = new Builder().name("gameplay").build();

    private final String name;

    private ChunkStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkStatus that = (ChunkStatus) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static class Builder {

        private String name;

        public Builder name(String string) {
            this.name = string;
            return this;
        }

        public ChunkStatus build() {
            return new ChunkStatus(name);
        }
    }
}
