package unknowndomain.engine.i18n;

import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.ModContainer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class LocaleManager {

	public static final Pattern STANDARD_LANG_NAME = Pattern.compile("(\\w+)_(\\w+)");
	private final Locale defaultLocale = Locale.US;

	private Locale currentLocale = defaultLocale;

	private Map<Locale, Map<String, String>> localeMap = new HashMap<>();

	public static final LocaleManager INSTANCE = new LocaleManager();

	private LocaleManager() {
	}

	public String translation(String key) {
		if (!localeMap.containsKey(currentLocale)) {
			return translation(key, defaultLocale);
		}
		return translation(key, currentLocale);
	}

	public String translation(String key, Locale locale){
		if(!localeMap.containsKey(locale)){
			return key;
		}
		return localeMap.get(locale).get(key);
	}

	public void setLocale(Locale l) {
		currentLocale = l;
	}

	public Locale getLocale() {
		return currentLocale;
	}

	public void addTranslation(Locale targetLocale, String key, String value) {
		if (!localeMap.containsKey(targetLocale)) {
			localeMap.put(targetLocale, new HashMap<>());
		}
		localeMap.get(targetLocale).put(key,value);
	}

	private void register(Locale locale, String modid) {
		var mod = checkModId(modid);
		register(locale, mod);
	}

	private void register(Locale locale, ModContainer mod) {
		if(mod.getAssets().exists("assets", mod.getModId(), "lang", locale.toString().concat(".lang"))){
			try(var stream = mod.getAssets().openStream("assets", mod.getModId(), "lang", locale.toLanguageTag().concat(".lang"));
				var reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8").newDecoder()))){
				reader.lines().forEach(line-> LocaleManager.this.addTranslation(locale, line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1)));
			} catch (IOException e) {
				Platform.getLogger().warn(String.format("cannot read language file of locale %s from mod %s", locale, mod.getModId()), e);
			}
		}else{
			Platform.getLogger().warn(String.format("cannot find language file %s.lang from mod %s", locale.toString(), mod.getModId()));
		}
	}

	public void register(String modid) {
		var mod = checkModId(modid);
		register(mod);
	}

	public void register(ModContainer mod) {
		mod.getAssets().list("assets", mod.getModId(), "lang").forEach(path -> {
			if(path.getFileName().toString().endsWith(".lang")){
				String filename = path.getFileName().toString();
				var lang = filename.substring(0, filename.lastIndexOf("."));
				Locale locale = getLocaleFromLangFilename(lang);
				try(var reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))){
					reader.lines().forEach(line-> LocaleManager.this.addTranslation(locale, line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1)));
				} catch (IOException e) {
					Platform.getLogger().warn(String.format("cannot read language file %s.lang from mod %s", lang, mod.getModId()), e);
				}
			}
		});
	}

	private Locale getLocaleFromLangFilename(String lang) {
		var builder = new Locale.Builder();
		var match = STANDARD_LANG_NAME.matcher(lang);
		if(!match.matches()){
			throw new IllegalArgumentException(String.format("Language filename should at least match format <language>_<region>! Found: %s", lang));
		}
		builder.setLanguage(match.group(1).toLowerCase());
		builder.setRegion(match.group(2).toUpperCase());
		return builder.build();
	}

	public void reset(){
		localeMap.clear();
	}

	private ModContainer checkModId(String modid) {
		Optional<ModContainer> optional = Platform.getEngine().getModManager().getMod(modid);
		if(optional.isEmpty()){
			throw new IllegalArgumentException(String.format("cannot get mod container of mod %s", modid));
		}
		return optional.get();
	}

}
