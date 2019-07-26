package nullengine.server.launch;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nullengine.Engine;
import nullengine.Platform;
import nullengine.exception.UninitializationException;
import nullengine.server.EngineServerImpl;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class EngineBootstrap {
    public static void main(String[] args) {
        var optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        var configArg = optionparser.accepts("config").withRequiredArg().ofType(String.class).defaultsTo("config.json");

        var parsed = optionparser.parse(args);
        if(parsed.has("config")){
            var configPath = parsed.valueOf(configArg);
        }

        var engine = new EngineServerImpl(Path.of(""));
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
            throw new UninitializationException("Cannot initialize engine");
        }
    }
}
