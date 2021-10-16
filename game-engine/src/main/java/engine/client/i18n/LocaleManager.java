package engine.client.i18n;

import engine.Platform;
import engine.mod.ModContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class LocaleManager {

    public static final Locale DEFAULT_LOCALE = Locale.US;

    private Locale currentLocale = DEFAULT_LOCALE;

    private final Map<Locale, Map<String, String>> localeMap = new HashMap<>();

    public static final LocaleManager INSTANCE = new LocaleManager();

    private LocaleManager() {
    }

    public String translate(String key) {
        if (!localeMap.containsKey(currentLocale)) {
            return translate(key, DEFAULT_LOCALE);
        }
        return translate(key, currentLocale);
    }

    public String translate(String key, Locale locale) {
        if (!localeMap.containsKey(locale)) {
            return key;
        }
        return localeMap.get(locale).get(key);
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public Locale getLocale() {
        return currentLocale;
    }

    public void addTranslation(Locale targetLocale, String key, String value) {
        if (!localeMap.containsKey(targetLocale)) {
            localeMap.put(targetLocale, new HashMap<>());
        }
        localeMap.get(targetLocale).put(key, value);
    }

    private void register(Locale locale, String modid) {
        var mod = checkModId(modid);
        register(locale, mod);
    }

    private void register(Locale locale, ModContainer mod) {
        try (var stream = mod.getAssets().openStream("asset", mod.getId(), "lang", locale.toLanguageTag().concat(".lang")).orElseThrow();
             var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8.newDecoder()))) {
            reader.lines().forEach(line -> LocaleManager.this.addTranslation(locale, line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1)));
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("cannot read language file of locale %s from mod %s", locale, mod.getId()), e);
        } catch (NoSuchElementException e) {
            Platform.getLogger().warn(String.format("cannot find language file %s.lang from mod %s", locale.toString(), mod.getId()));
        }
    }

    public void register(String modid) {
        var mod = checkModId(modid);
        register(mod);
    }

    public void register(ModContainer mod) {
        mod.getAssets().list("asset", mod.getId(), "lang").forEach(path -> {
            if (path.getFileName().toString().endsWith(".lang")) {
                String filename = path.getFileName().toString();
                var lang = filename.substring(0, filename.lastIndexOf("."));
                Locale locale = I18n.deserializeLocaleCode(lang);
                try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    reader.lines().forEach(line -> LocaleManager.this.addTranslation(locale, line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1)));
                } catch (IOException e) {
                    Platform.getLogger().warn(String.format("cannot read language file %s.lang from mod %s", lang, mod.getId()), e);
                }
            }
        });
    }

    public void reset() {
        localeMap.clear();
    }

    private ModContainer checkModId(String modid) {
        Optional<ModContainer> optional = Platform.getEngine().getModManager().getMod(modid);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException(String.format("cannot get mod container of mod %s", modid));
        }
        return optional.get();
    }

    public Set<Locale> getAllLocales() {
        return localeMap.keySet();
    }

    public Map<Locale, Map<String, String>> getLocaleMap() {
        return localeMap;
    }

}
