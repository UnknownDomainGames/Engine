package unknowndomain.engine.game;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.world.ChunkStore;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;

public class GameServer extends GameCommon {
    protected Map<String, World> worlds;
    protected List<WorldCommon> internalWorlds;
    protected List<Thread> worldThreads;

    public GameServer(Config config, EventBus bus) {
        super(config, bus);
    }

    @Override
    public World spawnWorld(World.Config config) {
        if (config == null) {
            WorldCommon w = new WorldCommon(context, new ChunkStore(context));
            this.worlds.put("default", w);
            this.internalWorlds.add(w);
            this.worldThreads.add(new Thread(w));
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
    public void run() {
        super.run();
        for (Thread thread : this.worldThreads) {
            thread.start();
        }
    }

    public void terminate() {
        for (WorldCommon worldCommon : internalWorlds) {
            worldCommon.stop();
        }
        // TODO: unload mod/resource here
    }
}