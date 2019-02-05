package unknowndomain.engine.client.i18n;

public class I18n {
    public static String localize(String key, Object... args) {
        return LocaleManager.getInstance().localize(key, args);
    }
}
