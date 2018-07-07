package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    public Resource load(ResourceLocation path) {
        String p = String.format("assets/%s/%s", path.getDomain(), path.getPath());
        ZipEntry entry = zip.getEntry(p);
        if (entry == null) return null;

        return new ResourceBase(path) {
            @Override
            public InputStream open() throws IOException {
                return ResourseSourceZip.this.zip.getInputStream(entry);
            }
        };
    }

    @Override
    public String[] domains() {
        Predicate<Character> atoz = (c) -> c >= 'a' && c <= 'z';
        Set<String> assets = zip.stream()
                .map(ZipEntry::getName)
                .filter(z -> z.startsWith("assets")
                        && z.length() > 7
                        && atoz.test(z.charAt(7)))
                .map(z -> z.substring(7, z.indexOf('/', 7)))
                .collect(Collectors.toSet());
        return assets.toArray(new String[assets.size()]);
    }

    @Override
    public PackInfo info() {
        return null;
    }

    @Override
    public String type() {
        return "zip";
    }
}
