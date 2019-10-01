package nullengine.game;

import nullengine.Engine;
import nullengine.event.world.WorldCreateEvent;
import nullengine.event.world.WorldLoadEvent;
import nullengine.registry.Registries;
import nullengine.world.World;
import nullengine.world.WorldCreationSetting;
import nullengine.world.exception.WorldAlreadyLoadedException;
import nullengine.world.exception.WorldProviderNotFoundException;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Each world host in an independent thread.
 */
public class GameServerFullAsync extends GameBase {

    protected final Map<String, World> worlds = new HashMap<>();
//    protected List<Thread> worldThreads;

    public GameServerFullAsync(Engine engine, Path storagePath, GameData data) {
        super(engine, storagePath, data);
    }

    @Nonnull
    @Override
    public World createWorld(@Nonnull String providerName, @Nonnull String name, @Nonnull WorldCreationSetting creationConfig) {
        Validate.notEmpty(providerName);
        Validate.notEmpty(name);
        Validate.notNull(creationConfig);
        if (worlds.containsKey(name)) {
            throw new WorldAlreadyLoadedException(name);
        }

        var provider = Registries.getWorldProviderRegistry().getValue(providerName);
        if (provider == null) {
            throw new WorldProviderNotFoundException(providerName);
        }

        var world = provider.create(this, storagePath.resolve("world").resolve(name), name, creationConfig);
        getEventBus().post(new WorldCreateEvent(world));

        this.worlds.put(name, world);
        this.data.getWorlds().put(name, providerName);
        this.data.save();
        getEventBus().post(new WorldLoadEvent(world));

//        Thread thread = new Thread((Runnable) world);
//        thread.setName("World Thread - " + name);
//        this.worldThreads.add(thread);

        return world;
    }

    private World loadWorld(@Nonnull String name, @Nonnull String providerName) {
        Validate.notEmpty(name);
        if (worlds.containsKey(name)) {
            throw new WorldAlreadyLoadedException(name);
        }

        var provider = Registries.getWorldProviderRegistry().getValue(providerName);
        if (provider == null) {
            throw new WorldProviderNotFoundException(providerName);
        }

        World world = provider.load(this, storagePath.resolve("world").resolve(name));
        this.worlds.put(name, world);
        getEventBus().post(new WorldLoadEvent(world));
        return world;
    }

    // @Override
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    @Nullable
    @Override
    public Optional<World> getWorld(@Nonnull String name) {
        return Optional.ofNullable(worlds.get(name));
    }

    @Override
    protected void constructStage() {
        super.constructStage();
//        this.worldThreads = Lists.newArrayList();
    }

    @Override
    protected void finishStage() {
        super.finishStage();
        data.getWorlds().forEach(this::loadWorld);
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