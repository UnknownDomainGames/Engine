package unknowndomain.engine.mod.java.harvester.asm;

import org.objectweb.asm.*;
import unknowndomain.engine.mod.java.harvester.AnnotationParser;

import static org.objectweb.asm.Opcodes.*;

public class ClassHarvester extends ClassVisitor {

    private final AnnotationParser parser;

    public ClassHarvester(AnnotationParser parser) {
        super(ASM5);
        this.parser = parser;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        parser.type(name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        parser.startHarvestAnno(desc);
        return new AnnotationHarvester(parser);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        parser.member(name);
        return new FieldHarvester(parser);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        parser.member(name + desc);
        return new MethodHarvester(parser);
    }
}
