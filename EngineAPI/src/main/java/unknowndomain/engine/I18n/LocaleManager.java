package unknowndomain.engine.I18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleManager {

	private final Locale defaultLocale = Locales.en_us;

	private Locale currentLocale = defaultLocale;

	private Map<Locale, Map<String, String>> localeMap = new HashMap<Locale, Map<String, String>>();

	public static LocaleManager localeManager = new LocaleManager();

	private LocaleManager() {
	}

	public String translation(String key) {
		if (!localeMap.containsKey(currentLocale)) {
			if (!localeMap.get(defaultLocale).containsKey(key)) {
				return key;
			}
			return localeMap.get(defaultLocale).get(key);
		}
		return localeMap.get(currentLocale).get(key);
	}

	public void setLocale(Locale l) {
		currentLocale = l;
	}

	public Locale getLocale() {
		return currentLocale;
	}

	public void addlocaleMap(Locale locale, String key, String value) {
		Map<String, String> keyMap;
		if (localeMap.containsKey(locale)) {
			keyMap = localeMap.get(locale);
			keyMap.put(key, value);
		} else {
			keyMap = new HashMap<String, String>();
			keyMap.put(key, value);
			localeMap.put(locale, keyMap);
		}
	}

	private void register(Locale locale, String modid) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/assets/" + modid + "/lang/" + locale.toString().toLowerCase() + ".lang");
		if (is == null) {
			return;
		}
		Reader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		Object[] objects = reader.lines().toArray();
		for (Object object : objects) {
			String str = object.toString();
			String key = str.replaceFirst("=(.*)", "");
			String value = str.replaceFirst("(.*)=", "");
			this.addlocaleMap(locale, key, value);
		}
		is.close();
		isr.close();
		reader.close();
	}

	public void register(String modid) {
		Field[] fields = Locales.class.getFields();
		for (Field field : fields) {
			try {
				register((Locale) field.get(null), modid);
			} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			}
		}
	}

}
