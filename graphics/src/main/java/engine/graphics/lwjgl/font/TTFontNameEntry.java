package engine.graphics.lwjgl.font;

public final class TTFontNameEntry {
    private final int platform;
    private final int encoding;
    private final int language;
    private final int name;
    private final String string;

    public TTFontNameEntry(int platform, int encoding, int language, int name, String string) {
        this.platform = platform;
        this.encoding = encoding;
        this.language = language;
        this.name = name;
        this.string = string;
    }

    public int getPlatform() {
        return platform;
    }

    public int getEncoding() {
        return encoding;
    }

    public int getLanguage() {
        return language;
    }

    public int getName() {
        return name;
    }

    public String getString() {
        return string;
    }

    public boolean match(int platform, int encoding, int language, int name) {
        return this.platform == platform && this.encoding == encoding && this.language == language && this.name == name;
    }
}
