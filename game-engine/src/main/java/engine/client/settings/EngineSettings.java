package engine.client.settings;

import configuration.Config;
import configuration.io.ConfigIOUtils;

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
        var config = ConfigIOUtils.load(path.toAbsolutePath());
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
        config.save(path.toAbsolutePath());
        lastPath = path;
    }

    public void apply() {
        displaySettings.apply();
    }
}
