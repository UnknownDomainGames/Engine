package unknowndomain.engine.mod.java.harvester.asm;

import org.objectweb.asm.*;
import unknowndomain.engine.mod.java.harvester.AnnotationParser;

import static org.objectweb.asm.Opcodes.*;

public class AnnotationHarvester extends AnnotationVisitor {

    private final AnnotationParser parser;

    public AnnotationHarvester(AnnotationParser parser) {
        super(ASM5);
        this.parser = parser;
    }

    @Override
    public void visit(String name, Object value) {
        parser.add(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return super.visitArray(name);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        parser.addEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnd() {
        parser.endHarvestAnno();
    }
}
