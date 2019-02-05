package unknowndomain.engine.client.i18n;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;

/**
 * keeps all locales
 *
 */
public class LocaleManager {

    private static LocaleManager instance;

    private Hashtable<Locale, Hashtable<String, String>> localeToMap = new Hashtable<>();

    private Locale currentLocale = null;

    public String localize(String key, Object... args) {
        if (currentLocale == null) {
            setLocale(Locale.getDefault());
        }
        String unformat = null;
        // search for locale
        loadLocale(currentLocale);
        Hashtable<String, String> table = localeToMap.get(currentLocale);
        if (table != null) {
            unformat = table.get(key);
        }

        if (unformat == null) {
            return key;
        }

        return String.format(unformat, args);
    }

    public void setLocale(Locale l) {
        currentLocale = l;
    }

    public void addMappingFor(Locale l, String key, String value) {
        loadLocale(l);
        Hashtable<String, String> table = localeToMap.get(l);
        table.put(key, value);
    }

    public void loadLocale(Locale l) {
        if (localeToMap.containsKey(l))
            return;
        Hashtable<String, String> table = new Hashtable<>();
        // TODO load with AssetsManager
        if (l.getLanguage().equals("en")) {
            table.put("test.key", "Test Key");
        } else {
            table.put("test.key", "\u6d4b\u8bd5");
        }
        localeToMap.put(l, table);

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
