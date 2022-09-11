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
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class LocaleManager {

    public static final String DEFAULT_LOCALE = "en_us";
    public static final LocaleManager INSTANCE = new LocaleManager();

    private static final Function<String, String> loadConvert;

    static {
        try {
            var methodLoadConvert = Properties.class.getDeclaredMethod("loadConvert", char[].class, int.class, int.class, StringBuilder.class);
            methodLoadConvert.setAccessible(true);
            var properties = new Properties();
            var buffer = new StringBuilder();
            loadConvert = (str) -> {
                var chars = str.toCharArray();
                try {
                    return methodLoadConvert.invoke(properties, chars, 0, chars.length, buffer).toString();
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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

    private void loadFrom(BufferedReader reader) {
        AtomicInteger lineNumberCounter = new AtomicInteger();
        AtomicReference<Pair<String, StringBuilder>> pair = new AtomicReference<>();
        reader.lines().forEach(line -> {
            var lineNumber = lineNumberCounter.incrementAndGet();
            try {
                line = line.stripLeading();
                if (line.isEmpty() || line.startsWith("#"))
                    return;
                if (pair.get() != null) {
                    pair.get().getValue().append(line.substring(0, line.length() - 1)).append('\n');
                    if (!line.endsWith("\\") || line.endsWith("\\\\")) {
                        localeMap.put(pair.get().getKey(), loadConvert.apply(pair.get().getValue().toString().stripTrailing()));
                        pair.set(null);
                    }
                } else {
                    var separator = line.indexOf("=");
                    if (separator == -1)
                        throw new IllegalArgumentException("Line without separator(\"=\"): " + line);
                    var key = line.substring(0, separator);
                    var value = line.substring(separator + 1);
                    if (value.endsWith("\\") && !value.endsWith("\\\\")) {
                        pair.set(Pair.of(key, new StringBuilder(value.substring(0, value.length() - 1) + "\n")));
                    } else {
                        localeMap.put(key, loadConvert.apply(value));
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid line " + lineNumber, e);
            }
        });
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
