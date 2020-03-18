package engine.graphics.lwjgl.font;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LCIDHelper {

    public static final LCIDHelper INSTANCE = new LCIDHelper();

    private final Map<Locale, Integer> localeToidMap = new HashMap<>();
    private final Multimap<Integer, Locale> idToLocaleMap = HashMultimap.create();

    public LCIDHelper() {
        if (SystemUtils.IS_OS_WINDOWS) {
            load("MS_LCID.csv");
        } else {
            throw new UnsupportedOperationException("Unsupported operation system");
        }
    }

    private void load(String resourceName) {
        try (InputStream inputStream = LCIDHelper.class.getResourceAsStream("/font/" + resourceName)) {
            IOUtils.readLines(inputStream, StandardCharsets.UTF_8).forEach(line -> {
                String[] split = line.split(",");
                Locale locale = Locale.forLanguageTag(split[0]);
                Integer id = Integer.valueOf(split[1], 16);
                localeToidMap.put(locale, id);
                idToLocaleMap.put(id, locale);
            });
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load language id", e);
        }
    }

    public int getLanguageID(Locale locale) {
        return localeToidMap.get(locale);
    }

    public Collection<Locale> getLocale(int languageId) {
        return idToLocaleMap.get(languageId);
    }
}
