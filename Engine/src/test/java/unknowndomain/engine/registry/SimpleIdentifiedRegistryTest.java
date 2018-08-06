package unknowndomain.engine.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.unclassified.BlockObjectBuilder;

public class SimpleIdentifiedRegistryTest {
    @Test
    void registerOne() {
        SimpleIdentifiedRegistry<BlockObject> registry = new SimpleIdentifiedRegistry<>();
        ResourcePath path = new ResourcePath("test", "test");
        BlockObject object = BlockObjectBuilder.create().setPath(path).build();
        registry.register(object);
        assertEquals(object, registry.getValue(path));
        assertEquals(path, registry.getKey(object));
        int id = registry.getId(path);
        assertEquals(id, registry.getId(object));
        assertEquals(0, id);
    }

    @Test
    void registerTwo() {
        SimpleIdentifiedRegistry<BlockObject> registry = new SimpleIdentifiedRegistry<>();
        ResourcePath path = new ResourcePath("test", "test1"), another = new ResourcePath("test", "test2");
        BlockObject object = BlockObjectBuilder.create().setPath(path).build(),
                anotherObject = BlockObjectBuilder.create().setPath(another).build();
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
        SimpleIdentifiedRegistry<BlockObject> registry = new SimpleIdentifiedRegistry<>();
        ResourcePath path = new ResourcePath("test", "test");
        BlockObject object = BlockObjectBuilder.create().setPath(path).build();
        registry.register(object);
        assertThrows(RegisterException.class, () -> {
            registry.register(object);
        });

        BlockObject another = BlockObjectBuilder.create().setPath(path).build();
        assertThrows(RegisterException.class, () -> {
            registry.register(another);
        });
    }
}