package unknowndomain.engine.api.util.translation;

import unknowndomain.engine.api.resource.ResourceLocation;

public interface LanguageMap {
    default String format(String key) {
        return format(new ResourceLocation(key));
    }

    default String format(ResourceLocation key, Object... params) {
        return String.format(format(key), params);
    }

    default String format(String key, Object... params) {
        return String.format(format(key), params);
    }

    /**
     * Localize a key to current selected locale.
     * If it is not exist in current locale, fallback to English (en_US) Locale or return key instead.
     * @param key Translation key, grouped in Mod.
     * @return Localized message
     */
    String format(ResourceLocation key);

    boolean hasKey(ResourceLocation key);
}
