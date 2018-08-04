package unknowndomain.engine.util.translation;

import javax.annotation.Nonnull;

public final class I18n {
    @Nonnull
    public static LanguageMap getLanguageMap() {
        return null; // TODO Inject JsonLanguageMap here
    }

    public static String format(String key) {
        return getLanguageMap().format(key);
    }

    public static String format(String domain, String key, Object... params) {
        return getLanguageMap().format(key, params);
    }

    public static String format(String key, Object... params) {
        return getLanguageMap().format(key, params);
    }

    public static String format(String domain, String key) {
        return getLanguageMap().format(key);
    }
}
