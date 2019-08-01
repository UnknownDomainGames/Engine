package nullengine.mod.init.handler;

import com.google.gson.reflect.TypeToken;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.annotation.data.AutoRegisterItem;
import nullengine.mod.init.ModInitializationTask;
import nullengine.mod.init.ModInitializer;
import nullengine.registry.Registry;
import nullengine.registry.RegistryEntry;
import nullengine.registry.RegistryManager;
import nullengine.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class RegistrationTask implements ModInitializationTask {

    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        var registryManager = initializer.getEngine().getRegistryManager();
        mod.getEventBus().post(new ModRegistrationEvent.Construction(registryManager));
        mod.getEventBus().post(new ModRegistrationEvent.Pre(registryManager));
        for (Map.Entry<Class<?>, Registry<?>> registry : registryManager.getEntries()) {
            mod.getEventBus().post(new ModRegistrationEvent.Register<>(registry.getValue()));
        }
        doAutoRegister(registryManager, mod);
        mod.getEventBus().post(new ModRegistrationEvent.Post(registryManager));
    }

    private void doAutoRegister(RegistryManager registryManager, ModContainer mod) {
        try {
            Optional<InputStream> stream = mod.getAssets().openStream("META-INF", "data", "AutoRegister.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("Not found \"AutoRegister.json\" file, skip AutoRegister stage.");
                return;
            }

            List<AutoRegisterItem> items;
            try (Reader reader = new InputStreamReader(stream.get())) {
                items = JsonUtils.gson().fromJson(reader, new TypeToken<List<AutoRegisterItem>>() {
                }.getType());
            }

            for (AutoRegisterItem item : items) {
                switch (item.getKind()) {
                    case CLASS:
                        doAutoRegisterClass(registryManager, mod, item);
                        break;
                    case FIELD:
                        doAutoRegisterField(registryManager, mod, item);
                        break;
                }
            }
        } catch (IOException e) {
            mod.getLogger().warn("Cannot open \"AutoRegister.json\" file, skip AutoRegister stage.", e);
        } catch (Exception e) {
            mod.getLogger().warn("Caught exception when auto register, skip AutoRegister stage.", e);
        }
    }

    private void doAutoRegisterClass(RegistryManager registryManager, ModContainer mod, AutoRegisterItem item) {
        try {
            Class<?> clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            for (Field field : clazz.getDeclaredFields()) {
                if (!RegistryEntry.class.isAssignableFrom(field.getType()))
                    continue;

                field.setAccessible(true);
                registryManager.register((RegistryEntry) field.get(null));
            }
        } catch (ReflectiveOperationException e) {
            mod.getLogger().warn(format("Cannot auto register class %s.", item.getOwner()), e);
        }
    }

    private void doAutoRegisterField(RegistryManager registryManager, ModContainer mod, AutoRegisterItem item) {
        try {
            Class<?> clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            Field field = clazz.getDeclaredField(item.getField());
            field.setAccessible(true);
            registryManager.register((RegistryEntry) field.get(null));
        } catch (ReflectiveOperationException e) {
            mod.getLogger().warn(format("Cannot auto register field %s.%s.", item.getOwner(), item.getField()), e);
        }
    }
}
