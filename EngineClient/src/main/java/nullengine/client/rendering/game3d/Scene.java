package nullengine.client.rendering.game3d;

import nullengine.client.rendering.light.LightManager;
import nullengine.client.rendering.material.Material;
import nullengine.world.World;

public class Scene {

    private World world;

    private LightManager lightManager = new LightManager();
    private Material material;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isEqualsWorld(World world) {
        return this.world == world;
    }

    public LightManager getLightManager() {
        return lightManager;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void reset() {
        setWorld(null);
        setMaterial(null);
        getLightManager().clear();
    }
}
