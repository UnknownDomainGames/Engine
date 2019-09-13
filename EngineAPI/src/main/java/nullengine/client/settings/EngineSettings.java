package nullengine.client.settings;

public class EngineSettings {

    private DisplaySettings displaySettings;

    public DisplaySettings getDisplaySettings() {
        return displaySettings;
    }

    public void apply() {
        displaySettings.apply();
    }
}
