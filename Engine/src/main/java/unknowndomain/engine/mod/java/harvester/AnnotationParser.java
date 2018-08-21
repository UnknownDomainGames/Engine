package unknowndomain.engine.mod.java.harvester;

import org.objectweb.asm.Type;

import java.util.LinkedList;
import java.util.function.Consumer;

public class AnnotationParser {

    private final Consumer<HarvestedAnnotation> harvestedAnnotationConsumer;

    private Type currentClass;
    private String currentMember;
    private final LinkedList<HarvestedAnnotation.Builder> harvestingAnno = new LinkedList<>();
    private final LinkedList<String> childrenName = new LinkedList<>();

    public AnnotationParser(Consumer<HarvestedAnnotation> harvestedAnnotationConsumer) {
        this.harvestedAnnotationConsumer = harvestedAnnotationConsumer;
    }

    public void startHarvestAnno(String desc) {
        harvestingAnno.addLast(new HarvestedAnnotation.Builder(currentClass, currentMember, Type.getType(desc)));
    }

    public void type(String type) {
        this.currentClass = Type.getType(type);
        this.currentMember = null;
    }

    public void member(String member) {
        this.currentMember = member;
    }

    public void add(String name, Object value) {
        harvestingAnno.getLast().info(name, value);
    }

    public void addEnum(String name, String desc, String value) {
        harvestingAnno.getLast().info(name, new EnumWrapper(Type.getType(desc), value));
    }

    public void addAnno(String name, String desc) {
        harvestingAnno.addLast(new HarvestedAnnotation.Builder(null, null, Type.getType(desc)));
    	childrenName.addLast(name);
    }

    public void endHarvestAnno() {
    	HarvestedAnnotation bakedAnno = harvestingAnno.removeLast().build();
    	if(isParent()) {
    		harvestedAnnotationConsumer.accept(bakedAnno);
    	} else {
    		HarvestedAnnotation.Builder parentAnno = harvestingAnno.getLast();
    		String name = childrenName.removeLast();
    		parentAnno.info(name, bakedAnno);
    	}
    }
    
    private boolean isParent() {
    	return childrenName.isEmpty();
    }
}
