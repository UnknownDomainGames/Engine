package nullengine.mod.init.task;

import com.google.gson.JsonArray;
import nullengine.mod.ModContainer;
import nullengine.mod.annotation.AutoListen;
import nullengine.mod.init.ModInitializer;
import nullengine.util.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;

public class AutoListenTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        JsonArray listeners;

        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "AutoListen.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("Not found \"AutoListen.json\" file, skip AutoListen stage.");
                return;
            }


            try (var reader = new InputStreamReader(stream.get())) {
                listeners = JsonUtils.parser().parse(reader).getAsJsonArray();
            }

        } catch (IOException e) {
            mod.getLogger().warn("Cannot open \"AutoListen.json\" file, skip AutoListen stage.", e);
            return;
        }

        for (var listener : listeners) {
            try {
                var listenerClass = Class.forName(listener.getAsString(), true, mod.getClassLoader());
                var anno = listenerClass.getAnnotation(AutoListen.class);
                if (anno.clientOnly() && !initializer.getEngine().isClient()) {
                    continue;
                }

                switch (anno.bus()) {
                    case ENGINE:
                    default:
                        initializer.getEngine().getEventBus().register(listenerClass);
                        break;
                    case MOD:
                        mod.getEventBus().register(listenerClass);
                        break;
                }
            } catch (ReflectiveOperationException e) {
                mod.getLogger().warn(String.format("Cannot register listener %s.", listener.getAsString()), e);
            }
        }
    }
}
