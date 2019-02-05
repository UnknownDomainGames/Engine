package unknowndomain.engine.client.i18n;

import java.util.Hashtable;

/**
 * A single locale and its localization
 *
 */
public class Locale {
    private String language;
    private String country;
    private Hashtable<String, String> localization;

    public Locale(String lang, String coun) {
        language = lang;
        country = coun;
        localization = new Hashtable<>();
    }

    public String toString() {
        return language + "-" + country;
    }

    /**
     * Get language in 2 lower case letters
     * 
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get country in 2 upper case letters
     * 
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * Add a localization entry
     * 
     * @param key
     * @param value
     */
    public void addLocalizationEntry(String key, String value) {
        localization.put(key, value);
    }

    /**
     * Localize the key with the args
     * 
     * @param key
     * @param args
     * @return the localized string, or null if no matching key exists
     */
    public String localize(String key, Object... args) {
        String value = localization.get(key);
        if (value == null)
            return null;
        return String.format(null, value, args);
    }
}
