package engine.client.asset.source;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackedAssetMetadata {

    public static PackedAssetMetadata fromJson(JsonObject jo) {
        var builder = Builder.create();

        if (jo.has("name")) {
            builder.name(jo.get("name").getAsString());
        }

        if (jo.has("description")) {
            builder.description(jo.get("description").getAsString());
        }

        if (jo.has("logoFile")) {
            builder.logoFile(jo.get("logoFile").getAsString());
        }

        if (jo.has("url")) {
            builder.url(jo.get("url").getAsString());
        }

        if (jo.has("authors")) {
            List<String> authors = new ArrayList<>();
            jo.getAsJsonArray("authors").forEach(jsonElement -> authors.add(jsonElement.getAsString()));
            builder.authors(List.copyOf(authors));
        }

        if (jo.has("properties")) {
            Map<String, String> properties = new HashMap<>();
            jo.getAsJsonObject("properties").entrySet().forEach(entry -> properties.put(entry.getKey(), entry.getValue().getAsString()));
            builder.properties(Map.copyOf(properties));
        }

        return builder.build();
    }

    private final String name;
    private final String description;
    private final String logoFile;
    private final String url;
    private final List<String> authors;
    private final Map<String, String> properties;

    public PackedAssetMetadata(String name, String description, String logoFile, String url, List<String> authors, Map<String, String> properties) {
        this.name = name;
        this.description = description;
        this.logoFile = logoFile;
        this.url = url;
        this.authors = authors;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoFile() {
        return logoFile;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static final class Builder {
        private String name;
        private String description;
        private String logoFile;
        private String url;
        private List<String> authors;
        private Map<String, String> properties;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder logoFile(String logoFile) {
            this.logoFile = logoFile;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public PackedAssetMetadata build() {
            return new PackedAssetMetadata(name, description, logoFile, url, authors, properties);
        }
    }
}
