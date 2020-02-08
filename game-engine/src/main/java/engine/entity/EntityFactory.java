package engine.entity;

import engine.world.World;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {

    T create(int id, World world, double x, double y, double z);
}
