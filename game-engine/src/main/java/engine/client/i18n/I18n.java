package engine.client.i18n;

public final class I18n {

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
}
