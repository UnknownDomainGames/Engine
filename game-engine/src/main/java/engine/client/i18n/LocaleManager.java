package engine.client.i18n;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import engine.Platform;
import engine.client.event.i18n.I18NPostFindDefinitionsEvent;
import engine.client.event.i18n.I18NPreFindDefinitionsEvent;
import engine.client.event.i18n.I18nPostLoadLocaleEvent;
import engine.client.event.i18n.I18nPreLoadLocaleEvent;
import engine.mod.ModContainer;
import engine.util.JsonUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LocaleManager {
    public static final String DEFAULT_LOCALE = "en_us";
    public static final LocaleManager INSTANCE = new LocaleManager();

    private LocaleDefinition defaultLocale;
    private LocaleDefinition currentLocale = null;

    private final Map<String, LocaleDefinition> localeDefinitions = new HashMap<>();
    private final Map<String, String> localeMap = new HashMap<>();

    private LocaleManager() {
    }

    public String translate(String key) {
        return localeMap.getOrDefault(key, key);
    }

    public @Nullable
    LocaleDefinition getLocaleDefinition(String code) {
        return localeDefinitions.get(code);
    }

    public void reloadAssets() {
        clear();
        findLocaleDefinitions();
        load(defaultLocale);
        { // Load current locale
            if (currentLocale == null) {
                currentLocale = getLocaleDefinition(Platform.getEngineClient().getSettings().getLanguage());
            } else {
                currentLocale = getLocaleDefinition(currentLocale.getCode()); // Re-bind
            }
            if (currentLocale == null) {
                currentLocale = defaultLocale;
            }
            if (currentLocale != defaultLocale) {
                load(currentLocale);
            }
        }
    }

    public void clear() {
        localeDefinitions.clear();
        localeMap.clear();
    }

    public void findLocaleDefinitions() {
        var engine = Platform.getEngine();
        engine.getEventBus().post(new I18NPreFindDefinitionsEvent());
        engine.getModManager().getLoadedMods().forEach(this::findLocaleDefinitions);
        defaultLocale = getLocaleDefinition(DEFAULT_LOCALE);
        engine.getEventBus().post(new I18NPostFindDefinitionsEvent());
    }

    public void findLocaleDefinitions(ModContainer mod) {
        mod.getAssets().get("asset", mod.getId(), "lang", "locales.json").ifPresent(langFile -> {
            try {
                var definitions = JsonUtils.gson().fromJson(Files.readString(langFile, StandardCharsets.UTF_8), JsonArray.class);
                for (var definition : definitions) {
                    LocaleDefinition localeDefinition = JsonUtils.gson().fromJson(definition, LocaleDefinition.class);
                    localeDefinitions.put(localeDefinition.getCode(), localeDefinition);
                }
            } catch (IOException | JsonSyntaxException e) {
                Platform.getLogger().warn("Error finding locale definitions in " + mod.getId(), e);
            }
        });
    }

    public void load(LocaleDefinition locale) {
        var engine = Platform.getEngine();
        engine.getEventBus().post(new I18nPreLoadLocaleEvent(locale));
        engine.getModManager().getLoadedMods().forEach(mod -> load(locale, mod));
        engine.getEventBus().post(new I18nPostLoadLocaleEvent(locale));
    }

    private void load(LocaleDefinition locale, ModContainer mod) {
        mod.getAssets().get("asset", mod.getId(), "lang", locale.getCode() + ".lang").ifPresent(langFile -> {
            try (var reader = Files.newBufferedReader(langFile, StandardCharsets.UTF_8)) {
                loadFrom(reader);
            } catch (IOException e) {
                Platform.getLogger().warn(String.format("Error loading language data for %s in %s", locale.getCode(), mod.getId()), e);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadFrom(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        localeMap.putAll((Map) properties);
    }

    public void setLocale(LocaleDefinition locale) {
        currentLocale = locale;
    }

    public LocaleDefinition getDefaultLocale() {
        return defaultLocale;
    }

    public LocaleDefinition getCurrentLocale() {
        return currentLocale;
    }

    public Map<String, LocaleDefinition> getLocaleDefinitions() {
        return localeDefinitions;
    }

    public Map<String, String> getLocaleMap() {
        return localeMap;
    }
}
