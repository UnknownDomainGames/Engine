package engine.mod.init.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import engine.mod.ModContainer;
import engine.mod.annotation.AutoListen;
import engine.mod.init.ModInitializer;

import java.io.IOException;
import java.io.InputStreamReader;

public class AutoListenTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        JsonArray listeners;

        try {
            var stream = mod.getAssets().openStream("META-INF", "data", "AutoListen.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("File \"AutoListen.json\" not found, skip AutoListen stage.");
                return;
            }


            try (var reader = new InputStreamReader(stream.get())) {
                listeners = JsonParser.parseReader(reader).getAsJsonArray();
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
