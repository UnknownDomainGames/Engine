package unknowndomain.engine.util.translation;

import com.google.common.collect.Lists;
import unknowndomain.engine.api.Platform;
import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.resource.ResourceLocation;
import unknowndomain.engine.api.resource.file.FileResource;
import unknowndomain.engine.api.resource.file.LanguageResource;
import unknowndomain.engine.api.util.translation.LanguageMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JsonLanguageMap implements LanguageMap {
    public Map<String, Map<Locale, Map<String, String>>> languageMap = new HashMap<>();

    public void reload() {
        Lists.reverse(Platform.getGame().getResourcePackManager().getResources()).forEach(resourcePack -> resourcePack.getResources().forEach(resource -> {
            FileResource fileResource = (FileResource) resource;
            if (fileResource.toJsonResource() != null && fileResource.toJsonResource().toLanguageResource() != null) {
                LanguageResource languageResource = fileResource.toJsonResource().toLanguageResource();

                Map<Locale, Map<String, String>> localeMapMap = languageMap.getOrDefault(languageResource.getResourceDomain(), new HashMap<>());
                Map<String, String> stringMap = localeMapMap.getOrDefault(languageResource.getLocale(), new HashMap<>());
                stringMap.putAll(languageResource.getLanguageMap());
                localeMapMap.put(languageResource.getLocale(), stringMap);
                languageMap.put(languageResource.getResourceDomain(), localeMapMap);
            }
        }));
    }

    @Override
    public String format(ResourceLocation key) {
        try {
            String formatted = languageMap.get(key.getDomain()).get(Platform.getLocale()).get(key.getPath());
            return formatted == null ? key.toString() : formatted;
        } catch (NullPointerException e) {
            return key.toString();
        }
    }

    @Override
    public boolean hasKey(ResourceLocation key) {
        try {
            return languageMap.get(key.getDomain()).get(Platform.getLocale()).containsKey(key.getPath());
        } catch (NullPointerException e) {
            return false;
        }
    }
}
