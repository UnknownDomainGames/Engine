package engine.client.i18n;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.regex.Pattern;

public final class I18n {

    public static final Pattern STANDARD_LANG_NAME = Pattern.compile("(\\w+)_(\\w+)");

    public static String translate(String key) {
        return LocaleManager.INSTANCE.translate(key);
    }

    public static String format(String key, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = translate((String) args[i]);
            }
        }
        return String.format(translate(key), args);
    }

    public static String serializeLocaleCode(Locale locale) {
        return locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase();
    }

    public static Locale deserializeLocaleCode(String code) {
        var match = STANDARD_LANG_NAME.matcher(code);
        if (!match.matches()) {
            throw new IllegalArgumentException(String.format("Language code should at least match format <language>_<region>! Found: %s", code));
        }
        var builder = new Locale.Builder();
        builder.setLanguage(match.group(1).toLowerCase());
        builder.setRegion(match.group(2).toUpperCase());
        return builder.build();
    }

    public static Locale findLocale(@Nullable String code) {
        if(code != null) {
            var match = STANDARD_LANG_NAME.matcher(code);
            if (match.matches()) {
                var builder = new Locale.Builder();
                builder.setLanguage(match.group(1).toLowerCase());
                builder.setRegion(match.group(2).toUpperCase());
                return builder.build();
            }
        }
        return LocaleManager.DEFAULT_LOCALE;
    }
}
