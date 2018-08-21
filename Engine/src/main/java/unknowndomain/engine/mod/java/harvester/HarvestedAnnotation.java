package unknowndomain.engine.mod.java.harvester;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.Type;

public class HarvestedAnnotation {

	private final Type ownerType;
	private final String memberName;
    private final Type type;
    private final Map<String, Object> harvestedInfo;

    private HarvestedAnnotation(Type ownerType, String memberName, Type type, Map<String, Object> harvestedInfo) {
    	this.ownerType = ownerType;
    	this.memberName = memberName;
        this.type = type;
        this.harvestedInfo = harvestedInfo;
    }

    public Type getOwnerType() {
        return ownerType;
    }

    public String getMemberName() {
        return memberName;
    }

    public Type getAnnotationType() {
        return type;
    }

    public Map<String, Object> getHarvestedInfo() {
        return harvestedInfo;
    }

    public static class Builder {
        private final Type ownerType;
        private final String memberName;
        private final Type type;
        private final Map<String, Object> harvestedInfo = new HashMap<>();

        public Builder(Type ownerType, String memberName, Type type) {
            this.ownerType = ownerType;
            this.memberName = memberName;
            this.type = type;
        }

        public void info(String name, Object value) {
            harvestedInfo.put(name, value);
        }

        public HarvestedAnnotation build() {
            return new HarvestedAnnotation(ownerType, memberName, type, ImmutableMap.copyOf(harvestedInfo));
        }
    }
}
