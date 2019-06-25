package nullengine.mod.init.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import nullengine.Engine;
import nullengine.Platform;
import nullengine.mod.ModAssets;
import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationHandler;
import nullengine.mod.init.ModInitializer;
import nullengine.util.JsonUtils;
import nullengine.util.RuntimeEnvironment;
import nullengine.util.Side;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

public class InjectHandler implements ModInitializationHandler {

    @Override
    public void handle(ModInitializer initializer, ModContainer mod) {
        try {
            Optional<InputStream> stream = mod.getAssets().openStream("META-INF", "data", "Inject.json");
            if (stream.isEmpty()) {
                mod.getLogger().warn("Not found \"Inject.json\" file, skip Inject stage.");
                return;
            }

            try (Reader reader = new InputStreamReader(stream.get())) {
                JsonArray items = JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonArray();
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
