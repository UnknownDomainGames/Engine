package engine.client.i18n;

public class LocaleDefinition {

    private final String code;
    private final String name;
    private final String region;

    public LocaleDefinition(String code, String name, String region) {
        this.code = code;
        this.name = name;
        this.region = region;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }
}
