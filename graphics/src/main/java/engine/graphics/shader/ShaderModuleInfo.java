package engine.graphics.shader;




import java.util.ArrayList;
import java.util.List;

public class ShaderModuleInfo {
    private int glslVersion;
    private ShaderType shaderType;
    private List<Variable> variables;

    public ShaderType getShaderType() {
        return shaderType;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public static class Variable {
        private String name;
        private VariableType type;
        private List<VariableQualifier> qualifiers = new ArrayList<>();

        public String getName() {
            return name;
        }

        public VariableType getType() {
            return type;
        }

        public List<VariableQualifier> getQualifiers() {
            return qualifiers;
        }
    }

    public static abstract class VariableType {
        private String type;

        public VariableType(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format("VariableType: %s", type);
        }
    }

    public static class ScalarVariableType extends VariableType {

        public ScalarVariableType(String type) {
            super(type);
        }
    }

    public static class VectorVariableType extends VariableType {

        private int rows;
        private int columns;

        public VectorVariableType(int rows, int columns) {
            super(columns > 1 ? "mat" : "vec");
            this.rows = rows;
            this.columns = columns;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }

        @Override
        public String toString() {
            return String.format("VariableType: %s", getType().concat(columns == 1 || columns == rows ? String.valueOf(rows) : String.format("%sx%s", columns, rows)));
        }
    }

    public static class ArrayVariableType extends VariableType {

        private VariableType elementType;
        /**
            The declared length of the array

         note that some of the array is allowed to have size not declared, which would have this variable set to -1
         */
        private int declaredLength;

        public ArrayVariableType(VariableType elementType, int length) {
            super("array:".concat(elementType.getType()));
            this.elementType = elementType;
            this.declaredLength = length;
        }

        public VariableType getElementType() {
            return elementType;
        }

        public int getDeclaredLength() {
            return declaredLength;
        }

        @Override
        public String toString() {
            String type = "";
            VariableType innertype = this;
            while(true) {
                if (innertype instanceof ArrayVariableType) {
                    var length = ((ArrayVariableType) innertype).getDeclaredLength();
                    type = type.concat(length != -1 ? "[" + length + "]" : "[]");
                    innertype = ((ArrayVariableType) innertype).getElementType();
                } else {
                    type = innertype.getType().concat(type);
                    break;
                }
            }
            return "VariableType: " + type;
        }
    }

    public static class StructVariableType extends VariableType {
        private List<Variable> variables;
        public StructVariableType(List<Variable> variables) {
            super("struct");
            this.variables = variables;
        }

        public List<Variable> getVariables() {
            return variables;
        }
    }

    public static class OpaqueVariableType extends VariableType {

        public OpaqueVariableType(String type) {
            super(type);
        }
    }

    public static class VariableQualifier {
        private String qualifier;

        public VariableQualifier(String qualifier){
            this.qualifier = qualifier;
        }

        @Override
        public String toString() {
            return String.format("VariableQualifier: %s", qualifier);
        }
    }

    public static class LayoutVariableQualifier extends VariableQualifier{
        private int location;

        private int component;

        private int index;

        private int binding;
        private int descriptorSet;
        private int offset;

        public LayoutVariableQualifier(String qualifier) {
            super("layout");
        }

        public int getLocation() {
            return location;
        }

        public void setLocation(int location) {
            this.location = location;
        }

        public int getComponent() {
            return component;
        }

        public void setComponent(int component) {
            this.component = component;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getBinding() {
            return binding;
        }

        public void setBinding(int binding) {
            this.binding = binding;
        }

        public int getDescriptorSet() {
            return descriptorSet;
        }

        public void setDescriptorSet(int descriptorSet) {
            this.descriptorSet = descriptorSet;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}
