package nullengine.client.settings;

import configuration.Config;
import configuration.parser.ConfigParsers;

import java.nio.file.Path;

public class EngineSettings {

    private DisplaySettings displaySettings;

    private Path lastPath;

    public DisplaySettings getDisplaySettings() {
        return displaySettings;
    }

    public EngineSettings(){
        displaySettings = new DisplaySettings();
    }

    public void load(){
        load(lastPath);
    }

    public void load(Path path){
        var config = ConfigParsers.load(path.toAbsolutePath());
        displaySettings = new DisplaySettings();
        displaySettings.load(config.getConfig("display"));
        lastPath = path;
    }

    public void save(){
        save(lastPath);
    }

    public void save(Path path){
        var config = new Config();
        config.set("display", displaySettings.save());
        ConfigParsers.save(path.toAbsolutePath(), config);
        lastPath = path;
    }

    public void apply() {
        displaySettings.apply();
    }
}
