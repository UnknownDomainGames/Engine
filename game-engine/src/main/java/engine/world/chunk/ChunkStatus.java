package engine.world.chunk;

public final class ChunkStatus {
    /**
     * state this chunk is just created
     */
    public static final ChunkStatus EMPTY = of("empty");
    /**
     * state this chunk is ready for gameplay
     */
    public static final ChunkStatus GAMEPLAY = of("gameplay");

    public static ChunkStatus of(String name) {
        return new ChunkStatus(name);
    }

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
}
