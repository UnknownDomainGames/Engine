package unknowndomain.engine.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import unknowndomain.engine.Engine;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Each world host in an independent thread.
 */
public class GameServerFullAsync extends GameBase {

    protected Map<String, World> worlds;
    protected List<WorldCommon> internalWorlds;
    protected List<Thread> worldThreads;

    public GameServerFullAsync(Engine engine) {
        super(engine);
    }

    @Override
    public World spawnWorld(World.Config config) {
        if (config == null) {
            WorldCommon w = new WorldCommon(this,null,null);
            this.worlds.put("default", w);
            this.internalWorlds.add(w);
            Thread thread = new Thread(w);
            thread.setName("World Thread: default");
            this.worldThreads.add(thread);
            return w;
        }
        return null;
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
        this.internalWorlds = Lists.newArrayList();
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
        for (WorldCommon worldCommon : internalWorlds) {
            worldCommon.stop();
        }
        // TODO: unload mod/resource here
        super.tryTerminate();
    }
}