package unknowndomain.engine.client.resource.source;

import unknowndomain.engine.client.resource.ResourceSourceInfo;
import unknowndomain.engine.client.resource.ResourceSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The source represent the zip file
 */
public class ResourseSourceZip implements ResourceSource {
    private ZipFile zip;

    public ResourseSourceZip(ZipFile file) {
        this.zip = file;
    }

    @Override
    public boolean has(String path) {
        String p = String.format("assets/%s", path);
        ZipEntry entry = zip.getEntry(p);
        return entry != null;
    }

    @Override
    public InputStream open(String path) throws IOException {
        String p = String.format("assets/%s", path);
        ZipEntry entry = zip.getEntry(p);
        if (entry == null) return null;
        return ResourseSourceZip.this.zip.getInputStream(entry);
    }

    public String[] domains() {
        Predicate<Character> atoz = (c) -> c >= 'a' && c <= 'z';
        return zip.stream()
                .map(ZipEntry::getName)
                .filter(z -> z.startsWith("assets")
                        && z.length() > 7
                        && atoz.test(z.charAt(7)))
                .map(z -> z.substring(7, z.indexOf('/', 7))).distinct().toArray(String[]::new);
    }

    @Override
    public ResourceSourceInfo info() {
        return null;
    }

    @Override
    public String type() {
        return "zip";
    }
}
