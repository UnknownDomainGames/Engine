package unknowndomain.engine.api.resource.file;

import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceBase;

import java.nio.file.Path;

public class FileResource extends ResourceBase {
    private Path path;

    public FileResource(Path path) {
        this.path = path;
    }
    
    protected Path getJPath() {
    	return path;
    }

    public String toString() {
        return path.toString();
    }

    public JsonResource toJsonResource() {
        return getPath().endsWith(".json") ? new JsonResource(getJPath()) : null;
    }
}
