package nullengine.server.launch;

import joptsimple.OptionParser;
import nullengine.Engine;
import nullengine.Platform;
import nullengine.server.EngineServerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        engine.initEngine();
        engine.runEngine();
        var in = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(true){
                var s = in.readLine();
                if("/stop".equals(s)){
                    engine.terminate();
                    break;
                }
            }
        } catch (IOException e) {
            Platform.getLogger().warn("Cannot read console input!", e);
        }
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
