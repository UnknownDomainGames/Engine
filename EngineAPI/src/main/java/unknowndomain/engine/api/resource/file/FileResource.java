package unknowndomain.engine.api.resource.file;

import unknowndomain.engine.api.Platform;
import unknowndomain.engine.api.resource.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileResource implements Resource {
    private Path path;

    public FileResource(Path path) {
        this.path = path;
    }

    @Override
    public byte[] getContent() {
        try {
            return Files.readAllBytes(getJPath());
        } catch (IOException e) {
            Platform.getLogger().error("Cannot read resource " + getPath(), e);
            return new byte[0];
        }
    }

    @Override
    public String getPath() {
        return path.toString();
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
