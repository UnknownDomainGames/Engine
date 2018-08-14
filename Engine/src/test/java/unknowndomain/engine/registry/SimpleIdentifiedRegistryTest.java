package unknowndomain.engine.registry;

import org.junit.jupiter.api.Test;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleIdentifiedRegistryTest {
    @Test
    void registerOne() {
        SimpleIdentifiedRegistry<Block> registry = new SimpleIdentifiedRegistry<>();
        String path = "test";
        Block object = BlockBuilder.create(path).build();
        registry.register(object);
        assertEquals(object, registry.getValue(path));
        assertEquals(path, registry.getKey(object));
        int id = registry.getId(path);
        assertEquals(id, registry.getId(object));
        assertEquals(0, id);
    }

    @Test
    void registerTwo() {
        SimpleIdentifiedRegistry<Block> registry = new SimpleIdentifiedRegistry<>();
        String path = "test1", another = "test2";
        Block object = BlockBuilder.create(path).build(),
                anotherObject = BlockBuilder.create(another).build();
        registry.register(object);
        registry.register(anotherObject);

        assertEquals(object, registry.getValue(path));
        assertEquals(path, registry.getKey(object));

        assertEquals(anotherObject, registry.getValue(another));
        assertEquals(another, registry.getKey(anotherObject));

        int id = registry.getId(path);
        int anotherId = registry.getId(another);

        assertEquals(id, registry.getId(object));

        assertEquals(anotherId, registry.getId(anotherObject));

        assertEquals(0, id);
        assertEquals(1, anotherId);
    }

    @Test
    void registerDuplicated() {
        SimpleIdentifiedRegistry<Block> registry = new SimpleIdentifiedRegistry<>();
        String path = "test";
        Block object = BlockBuilder.create(path).build();
        registry.register(object);
        assertThrows(RegisterException.class, () -> {
            registry.register(object);
        });

        Block another = BlockBuilder.create(path).build();
        assertThrows(RegisterException.class, () -> {
            registry.register(another);
        });
    }
}