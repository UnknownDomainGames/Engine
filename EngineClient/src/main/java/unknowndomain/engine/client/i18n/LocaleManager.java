package unknowndomain.engine.client.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleManager {

	private final Locale defaultLocale = Locales.en_us;

	private Locale currentLocale = defaultLocale;

	private Map<Locale, Map<String, String>> localeMap = new HashMap<Locale, Map<String, String>>();

	public static LocaleManager localeManager = new LocaleManager();

	protected static String translation(String key) {
		if (!localeManager.localeMap.containsKey(localeManager.currentLocale)) {
			if (!localeManager.localeMap.get(localeManager.defaultLocale).containsKey(key)) {
				return key;
			}
			return localeManager.localeMap.get(localeManager.defaultLocale).get(key);
		}
		return localeManager.localeMap.get(localeManager.currentLocale).get(key);
	}

	public void setLocale(Locale l) {
		currentLocale = l;
	}

	public void addlocaleMap(Locale locale, String key, String value) {
		String modid = "";
		this.addlocaleMap(modid, locale, key, value);
	}

	private void addlocaleMap(String modid, Locale locale, String key, String value) {
		Map<String, String> keyMap;
		if (localeMap.containsKey(locale)) {
			keyMap = localeMap.get(locale);
			keyMap.put(modid + ":" + key, value);
		} else {
			keyMap = new HashMap<String, String>();
			keyMap.put(modid + ":" + key, value);
			localeMap.put(locale, keyMap);
		}
	}

}
