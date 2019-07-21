package nullengine.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nullengine.Engine;
import nullengine.registry.Registries;
import nullengine.world.World;
import nullengine.world.WorldCommon;
import nullengine.world.WorldCreationSetting;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Each world host in an independent thread.
 */
public class GameServerFullAsync extends GameBase {

    protected Map<String, World> worlds;
    protected List<Thread> worldThreads;

    public GameServerFullAsync(Engine engine, Path storagePath) {
        super(engine, storagePath);
    }

    @Override
    public World createWorld(String providerName, String name, WorldCreationSetting creationConfig) {
        var provider = Registries.getWorldProviderRegistry().getValue(providerName);
        var world = provider.create(this, storagePath.resolve(Path.of("world", name)), name, creationConfig);
        this.worlds.put(name, world);
        Thread thread = new Thread((Runnable) world);
        thread.setName("World Thread - " + name);
        this.worldThreads.add(thread);
        return world;
    }

    // @Override
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    @Nullable
    // @Override
    public World getWorld(String name) {
        return worlds.get(name);
    }

    @Override
    protected void constructStage() {
        this.worlds = Maps.newTreeMap();
        this.worldThreads = Lists.newArrayList();
        super.constructStage();
    }

    @Override
    public void init() {
        super.init();
        for (Thread thread : this.worldThreads) {
            thread.start();
        }
    }

    @Override
    public void terminate() {
        super.terminate();
    }

    @Override
    protected void tryTerminate() {
        for (World worldCommon : worlds.values()) {
            ((WorldCommon) worldCommon).stop();
        }
        // TODO: unload mod/resource here
        super.tryTerminate();
    }
}