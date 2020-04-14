package engine.client.asset.source;

import java.util.List;

public final class AssetBundleMetadata {

    private final String name;
    private final String description;
    private final String license;
    private final String logo;
    private final String url;
    private final List<String> authors;
    private final List<String> credits;

    public static Builder builder() {
        return new Builder();
    }

    private AssetBundleMetadata(String name, String description, String license, String logo, String url, List<String> authors, List<String> credits) {
        this.name = name;
        this.description = description;
        this.license = license;
        this.logo = logo;
        this.url = url;
        this.authors = authors;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license;
    }

    public String getLogo() {
        return logo;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getCredits() {
        return credits;
    }

    public static final class Builder {
        private String name;
        private String description;
        private String license;
        private String logo;
        private String url;
        private List<String> authors;
        private List<String> credits;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder license(String license) {
            this.license = license;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
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

        public Builder credits(List<String> credits) {
            this.credits = credits;
            return this;
        }

        public AssetBundleMetadata build() {
            return new AssetBundleMetadata(name, description, license, logo, url, authors, credits);
        }
    }
}
