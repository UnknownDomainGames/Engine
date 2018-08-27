package unknowndomain.engine.mod.java.harvester;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import unknowndomain.engine.mod.java.harvester.asm.ClassHarvester;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class HarvestedInfo {
    private final Multimap<Type, HarvestedAnnotation> harvestedAnnotations;

    private HarvestedInfo(Multimap<Type, HarvestedAnnotation> info) {
        harvestedAnnotations = info;
    }

    public static HarvestedInfo harvest(JarFile source) throws IOException {
        Multimap<Type, HarvestedAnnotation> harvestedAnnotations = HashMultimap.create();
        AnnotationParser annotationParser = new AnnotationParser(
                anno -> harvestedAnnotations.put(anno.getAnnotationType(), anno));

        ClassHarvester classHarvester = new ClassHarvester(annotationParser);
        try (JarFile jarFile = source) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class"))
                    startHarvestClass(jarFile.getInputStream(entry), classHarvester);
            }
        }
        return new HarvestedInfo(harvestedAnnotations);
    }

    private static void startHarvestClass(InputStream inputStream, ClassHarvester classHarvester) {
        try {
            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(classHarvester, 0);
        } catch (IOException e) {
            // ignore
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public Collection<HarvestedAnnotation> getHarvestedAnnotations(Class<?> clazz) {
        return harvestedAnnotations.get(Type.getType(clazz));
    }
}
