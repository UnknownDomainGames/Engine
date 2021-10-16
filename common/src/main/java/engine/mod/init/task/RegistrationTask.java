package engine.mod.init.task;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.reflect.TypeToken;
import engine.Platform;
import engine.event.mod.ModRegistrationEvent;
import engine.mod.ModContainer;
import engine.mod.annotation.RegObject;
import engine.mod.annotation.data.AutoRegisterItem;
import engine.mod.init.ModInitializer;
import engine.registry.Registrable;
import engine.registry.Registry;
import engine.registry.RegistryManager;
import engine.util.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RegistrationTask implements ModInitializationTask {

    private final Multimap<Class<?>, Registrable<?>> autoRegistrableEntries = HashMultimap.create();

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void run(ModInitializer initializer, ModContainer mod) {
        var registryManager = initializer.getEngine().getRegistryManager();
        mod.getEventBus().post(new ModRegistrationEvent.Construction(mod, registryManager));
        var pre = new ModRegistrationEvent.Pre(mod, registryManager);
        mod.getEventBus().post(pre);
        collectAutoRegistrable(mod);
        for (Registry registry : registryManager.getRegistries()) {
            mod.getEventBus().post(new ModRegistrationEvent.Register<>(registry));
            autoRegistrableEntries.get(registry.getEntryType()).forEach(registry::register);
        }
        var post = new ModRegistrationEvent.Post(mod, registryManager);
        mod.getEventBus().post(post);
        doInjectRegisteredObject(registryManager, mod);
    }

    private void collectAutoRegistrable(ModContainer mod) {
        autoRegistrableEntries.clear();
        for (var item : loadAutoRegisterItems(mod)) {
            switch (item.getKind()) {
                case CLASS:
                    collectAutoRegistrableFromClass(mod, item);
                    break;
                case FIELD:
                    collectAutoRegistrableFromField(mod, item);
                    break;
            }
        }
    }

    private List<AutoRegisterItem> loadAutoRegisterItems(ModContainer mod) {
        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "AutoRegister.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("File \"AutoRegister.json\" not found, skip AutoRegister stage.");
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

    private void collectAutoRegistrableFromClass(ModContainer mod, AutoRegisterItem item) {
        try {
            var clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            for (var field : clazz.getDeclaredFields()) {
                if (Registrable.class.isAssignableFrom(field.getType())) {
                    collectAutoRegistrableFromField(field);
                }
            }
        } catch (ReflectiveOperationException e) {
            Platform.getEngine().getCrashHandler().crash(e);
        }
    }

    private void collectAutoRegistrableFromField(ModContainer mod, AutoRegisterItem item) {
        try {
            var clazz = Class.forName(item.getOwner(), true, mod.getClassLoader());
            var field = clazz.getDeclaredField(item.getField());
            collectAutoRegistrableFromField(field);
        } catch (ReflectiveOperationException e) {
            Platform.getEngine().getCrashHandler().crash(e);
        }
    }

    private void collectAutoRegistrableFromField(Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Registrable<?> registrable = (Registrable<?>) field.get(null);
        autoRegistrableEntries.put(registrable.getEntryType(), registrable);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doInjectRegisteredObject(RegistryManager registryManager, ModContainer mod) {
        for (var entry : loadRegObjectItems(mod).entrySet()) {
            try {
                var clazz = Class.forName(entry.getKey(), true, mod.getClassLoader());
                for (var fieldName : entry.getValue()) {
                    var field = clazz.getDeclaredField(fieldName);
                    var annotation = field.getAnnotation(RegObject.class);
                    var registry = findRegistry(registryManager, (Class<? extends Registrable>) field.getType());
                    field.setAccessible(true);
                    field.set(null, registry.getValue(annotation.value()));
                }
            } catch (ReflectiveOperationException e) {
                Platform.getEngine().getCrashHandler().crash(e);
            }
        }
    }

    private <T extends Registrable<T>> Registry<T> findRegistry(RegistryManager registryManager, Class<T> type) {
        return registryManager.getRegistry(type).orElseThrow();
    }

    private Map<String, List<String>> loadRegObjectItems(ModContainer mod) {
        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "RegObject.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("File \"RegObject.json\" not found, skip inject registered object.");
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
