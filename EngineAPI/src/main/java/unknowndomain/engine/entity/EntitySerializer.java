package unknowndomain.engine.entity;

import unknowndomain.engine.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface EntitySerializer<T extends Entity> {

    T load(World world, DataInputStream dis, EntityEntry entityEntry);

    void save(World world, DataOutputStream dos, EntityEntry entityEntry, T entity);

}
