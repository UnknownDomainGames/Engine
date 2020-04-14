package engine.mod.init.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import engine.Engine;
import engine.Platform;
import engine.mod.ModAssets;
import engine.mod.ModContainer;
import engine.mod.annotation.ConfigPath;
import engine.mod.annotation.DataPath;
import engine.mod.init.ModInitializer;
import engine.util.RuntimeEnvironment;
import engine.util.Side;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Optional;

public class InjectionTask implements ModInitializationTask {

    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        try {
            Optional<InputStream> stream = mod.getAssets().openStream("META-INF", "data", "Inject.json");
            if (stream.isEmpty()) {
                mod.getLogger().debug("Not found \"Inject.json\" file, skip Inject stage.");
                return;
            }

            try (Reader reader = new InputStreamReader(stream.get())) {
                JsonArray items = JsonParser.parseReader(reader).getAsJsonArray();
                Guice.createInjector(
                        Platform.getRuntimeEnvironment() == RuntimeEnvironment.DEPLOYMENT ? Stage.PRODUCTION : Stage.DEVELOPMENT,
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(Engine.class).toInstance(initializer.getEngine());
                                bind(Side.class).toInstance(initializer.getEngine().getSide());
                                bind(RuntimeEnvironment.class).toInstance(initializer.getEngine().getRuntimeEnvironment());

                                bind(ModContainer.class).toInstance(mod);
                                bind((Class) mod.getInstance().getClass()).toInstance(mod.getInstance());
                                bind(Logger.class).toInstance(mod.getLogger());
                                bind(ModAssets.class).toInstance(mod.getAssets());
                                bind(Path.class).annotatedWith(ConfigPath.class).toInstance(mod.getConfigPath());
                                bind(Path.class).annotatedWith(DataPath.class).toInstance(mod.getDataPath());
                            }
                        },
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                for (JsonElement item : items) {
                                    try {
                                        requestStaticInjection(Class.forName(item.getAsString(), true, mod.getClassLoader()));
                                    } catch (ClassNotFoundException ignored) {
                                    }
                                }
                            }
                        }).injectMembers(mod.getInstance());
            }
        } catch (IOException e) {
            mod.getLogger().warn("Cannot open \"Inject.json\" file, skip Inject stage.", e);
        } catch (Exception e) {
            mod.getLogger().warn("Caught exception when inject, skip Inject stage.", e);
        }
    }
}
