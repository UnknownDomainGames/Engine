package unknowndomain.engine.mod.java.harvester.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import unknowndomain.engine.mod.java.harvester.AnnotationParser;

import static org.objectweb.asm.Opcodes.ASM5;

public class FieldHarvester extends FieldVisitor {

    private final AnnotationParser parser;

    public FieldHarvester(AnnotationParser parser) {
        super(ASM5);
        this.parser = parser;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    	parser.startHarvestAnno(desc);
        return new AnnotationHarvester(parser);
    }
}
