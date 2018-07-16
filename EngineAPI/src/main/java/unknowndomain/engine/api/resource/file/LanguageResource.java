package unknowndomain.engine.api.resource.file;

import com.google.common.reflect.TypeToken;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public class LanguageResource extends JsonResource {
    public LanguageResource(Path path) {
        super(path);
    }

    public String getResourceDomain() {
        String pathString = getPath().toString();
        return pathString.substring(pathString.indexOf("assets/") + "assets/".length(), pathString.indexOf("/lang/"));
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(FilenameUtils.getBaseName(getPath().getFileName().toString()));
    }

    public Map<String, String> getLanguageMap() {
        return deserialize(new TypeToken<Map<String, String>>(){}.getType());
    }
}
