package engine.server.launch;

import engine.Engine;
import engine.Platform;
import engine.server.EngineServerImpl;
import joptsimple.OptionParser;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class EngineBootstrap {
    public static void main(String[] args) {
        var optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        var runArg = optionparser.accepts("runDirectory").withRequiredArg().ofType(String.class).defaultsTo("");
        var configArg = optionparser.accepts("config").withRequiredArg().ofType(String.class).defaultsTo("config.json");

        var parsed = optionparser.parse(args);
        var configPath = "server.json";
        if(parsed.has("config")){
            configPath = parsed.valueOf(configArg);
        }

        var engine = new EngineServerImpl(Path.of(parsed.valueOf(runArg)), Path.of(configPath));
        injectEngine(engine);
        engine.start();
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
