package engine.client.settings;

import configuration.Config;
import configuration.io.ConfigIOUtils;
import engine.client.i18n.I18n;
import engine.client.i18n.LocaleManager;

import java.nio.file.Path;

public class EngineSettings {

    public static final String KEY_DISPLAY = "display";
    public static final String KEY_LANGUAGE = "language";

    private DisplaySettings displaySettings;

    private String language;

    private Path lastPath;

    public EngineSettings() {
        displaySettings = new DisplaySettings();
    }

    public void load() {
        load(lastPath);
    }

    public void load(Path path) {
        var config = ConfigIOUtils.load(path.toAbsolutePath());
        displaySettings = new DisplaySettings();
        displaySettings.load(config.getConfig(KEY_DISPLAY));
        language = config.getString(KEY_LANGUAGE);
        LocaleManager.INSTANCE.setLocale(I18n.findLocale(language));
        lastPath = path;
    }

    public void save() {
        save(lastPath);
    }

    public void save(Path path) {
        var config = new Config();
        config.set(KEY_DISPLAY, displaySettings.save());
        config.set(KEY_LANGUAGE, language);
        config.save(path.toAbsolutePath());
        lastPath = path;
    }

    public void apply() {
        displaySettings.apply();
        LocaleManager.INSTANCE.setLocale(I18n.findLocale(language));
    }

    public DisplaySettings getDisplaySettings() {
        return displaySettings;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
