package engine.entity;

import engine.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface EntitySerializer<T extends Entity> {

    T load(World world, DataInputStream dis, EntityProvider entityProvider);

    void save(World world, DataOutputStream dos, EntityProvider entityProvider, T entity);

}
