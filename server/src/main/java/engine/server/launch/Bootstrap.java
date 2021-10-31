package engine.server.launch;

import engine.Engine;
import engine.Platform;
import engine.player.Profile;
import engine.server.EngineServer;
import engine.server.EngineServerImpl;
import joptsimple.OptionParser;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

public class Bootstrap {

    public static void main(String[] args) {
        var optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        var runArg = optionParser.accepts("runDirectory").withRequiredArg().ofType(String.class).defaultsTo("");
        var configArg = optionParser.accepts("config").withRequiredArg().ofType(String.class).defaultsTo("server.json");

        var parsed = optionParser.parse(args);
        var configPath = "server.json";
        if(parsed.has("config")){
            configPath = parsed.valueOf(configArg);
        }

        var engine = new EngineServerImpl(Path.of(parsed.valueOf(runArg)), Path.of(configPath));
        injectEngine(engine);
        engine.initEngine();
        engine.runEngine();
    }

    private static void injectEngine(Engine engine) {
        try {
            Field engineField = Platform.class.getDeclaredField("engine");
            engineField.setAccessible(true);
            engineField.set(null, engine);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot initialize engine");
        }
    }
}
