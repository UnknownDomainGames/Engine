package unknowndomain.engine.client.i18n;

/**
 * keeps all locales
 *
 */
public class LocaleManager {

    private static LocaleManager instance;

    private Locale currentLocale = null;

    public String localize(String key, Object... args) {
        if (currentLocale == null) {

        }
        return null;
    }

    /**
     * Attempt to init the locale with java.util.Locale
     */
    private void initLocale() {
        java.util.Locale loc = java.util.Locale.getDefault();
        // TODO
    }

    public static LocaleManager getInstance() {
        if (instance == null)
            instance = new LocaleManager();
        return instance;
    }

    public static void setInstance(LocaleManager l) {
        instance = l;
    }
}
