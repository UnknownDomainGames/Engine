package nullengine.game;

import com.google.common.collect.Maps;
import nullengine.Engine;
import nullengine.event.world.WorldCreateEvent;
import nullengine.registry.Registries;
import nullengine.world.World;
import nullengine.world.WorldCreationSetting;
import nullengine.world.exception.WorldAlreadyExistsException;
import nullengine.world.exception.WorldProviderNotFoundException;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Each world host in an independent thread.
 */
public class GameServerFullAsync extends GameBase {

    protected Map<String, World> worlds;
//    protected List<Thread> worldThreads;

    public GameServerFullAsync(Engine engine, Path storagePath) {
        super(engine, storagePath);
    }

    @Override
    public World createWorld(@Nonnull String providerName, @Nonnull String name, @Nonnull WorldCreationSetting creationConfig) {
        Validate.notEmpty(providerName);
        Validate.notEmpty(name);
        Validate.notNull(creationConfig);
        if (worlds.containsKey(name)) {
            throw new WorldAlreadyExistsException(name);
        }

        var provider = Registries.getWorldProviderRegistry().getValue(providerName);
        if (provider == null) {
            throw new WorldProviderNotFoundException(providerName);
        }

        var world = provider.create(this, storagePath.resolve(Path.of("world", name)), name, creationConfig);
        this.worlds.put(name, world);

        getEventBus().post(new WorldCreateEvent(world));

//        Thread thread = new Thread((Runnable) world);
//        thread.setName("World Thread - " + name);
//        this.worldThreads.add(thread);

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
//        this.worldThreads = Lists.newArrayList();
        super.constructStage();
    }

    @Override
    public void init() {
        super.init();
//        for (Thread thread : this.worldThreads) {
//            thread.start();
//        }
    }

    @Override
    public void terminate() {
        super.terminate();
    }

    @Override
    protected void tryTerminate() {
//        for (World worldCommon : worlds.values()) {
//            ((WorldCommon) worldCommon).stop();
//        }
        worlds.values().forEach(World::unload);
        // TODO: unload mod/resource here
        super.tryTerminate();
    }
}