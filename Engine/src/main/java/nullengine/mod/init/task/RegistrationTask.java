package nullengine.mod.init.task;

import com.google.gson.reflect.TypeToken;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.annotation.RegObject;
import nullengine.mod.annotation.data.AutoRegisterItem;
import nullengine.mod.init.ModInitializer;
import nullengine.registry.Name;
import nullengine.registry.Registrable;
import nullengine.registry.Registry;
import nullengine.registry.RegistryManager;
import nullengine.util.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class RegistrationTask implements ModInitializationTask {

    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        var registryManager = initializer.getEngine().getRegistryManager();
        mod.getEventBus().post(new ModRegistrationEvent.Construction(mod, registryManager));
        var pre = new ModRegistrationEvent.Pre(mod, registryManager);
        mod.getEventBus().post(pre);
        initializer.getEngine().getEventBus().post(pre);
        for (Map.Entry<Class<?>, Registry<?>> registry : registryManager.getEntries()) {
            mod.getEventBus().post(new ModRegistrationEvent.Register<>(registry.getValue()));
        }
        doAutoRegister(registryManager, mod);
        var post = new ModRegistrationEvent.Post(mod, registryManager);
        mod.getEventBus().post(post);
        initializer.getEngine().getEventBus().post(post);
        doInjectRegisteredObject(registryManager, mod);
    }

    private void doAutoRegister(RegistryManager registryManager, ModContainer mod) {
        for (var item : loadAutoRegisterItems(mod)) {
            switch (item.getKind()) {
                case CLASS:
                    doAutoRegisterClass(registryManager, mod, item);
                    break;
                case FIELD:
                    doAutoRegisterField(registryManager, mod, item);
                    break;
            }
        }
    }

    private List<AutoRegisterItem> loadAutoRegisterItems(ModContainer mod) {
        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "AutoRegister.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("Not found \"AutoRegister.json\" file, skip AutoRegister stage.");
                return List.of();
            }

            try (var reader = new InputStreamReader(stream.get())) {
                return JsonUtils.gson().fromJson(reader, new TypeToken<List<AutoRegisterItem>>() {
                }.getType());
            }
        } catch (IOException e) {
            mod.getLogger().warn("Cannot open \"AutoRegister.json\" file, skip AutoRegister stage.", e);
        } catch (Exception e) {
            mod.getLogger().warn("Caught exception when auto register, skip AutoRegister stage.", e);
        }
        return List.of();
    }

    private void doAutoRegisterClass(RegistryManager registryManager, ModContainer mod, AutoRegisterItem item) {
        try {
            var clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            for (var field : clazz.getDeclaredFields()) {
                if (!Registrable.class.isAssignableFrom(field.getType()))
                    continue;

                field.setAccessible(true);
                registryManager.register((Registrable) field.get(null));
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
            // TODO: crash report
        }
    }

    private void doAutoRegisterField(RegistryManager registryManager, ModContainer mod, AutoRegisterItem item) {
        try {
            var clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            var field = clazz.getDeclaredField(item.getField());
            field.setAccessible(true);
            registryManager.register((Registrable) field.get(null));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
            // TODO: crash report
        }
    }

    private void doInjectRegisteredObject(RegistryManager registryManager, ModContainer mod) {
        for (var entry : loadRegObjectItems(mod).entrySet()) {
            try {
                var clazz = Class.forName(entry.getKey(), true, mod.getClassLoader());
                for (var fieldName : entry.getValue()) {
                    var field = clazz.getDeclaredField(fieldName);
                    var annotation = field.getAnnotation(RegObject.class);
                    var registry = findRegistry(registryManager, (Class<? extends Registrable>) field.getType());
                    field.setAccessible(true);
                    field.set(null, registry.getValue(Name.fromString(annotation.value())));
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
                // TODO: crash report
            }
        }
    }

    private Registry<?> findRegistry(RegistryManager registryManager, Class<? extends Registrable> type) {
        return (Registry<?>) registryManager.getRegistry(type).orElseThrow();
    }

    private Map<String, List<String>> loadRegObjectItems(ModContainer mod) {
        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "RegObject.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("Not found \"RegObject.json\" file, skip inject registered object.");
                return Map.of();
            }

            try (var reader = new InputStreamReader(stream.get())) {
                return JsonUtils.gson().fromJson(reader, new TypeToken<Map<String, List<String>>>() {
                }.getType());
            }
        } catch (IOException e) {
            mod.getLogger().warn("Cannot open \"RegObject.json\" file, skip inject registered object.", e);
        } catch (Exception e) {
            mod.getLogger().warn("Caught exception when inject registered object, skip it.", e);
        }
        return Map.of();
    }
}
