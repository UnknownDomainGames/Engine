package engine.block.state;

import engine.block.Block;
import engine.state.Property;
import engine.state.State;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockState extends State<Block, BlockState> {

    public BlockState(Block owner, Map<Property, Comparable> properties) {
        super(owner, properties);
    }

    public Block getPrototype(){
        return owner;
    }

    public String toStorageString(){
        if(getProperties().isEmpty()){
            return owner.getName().getUniqueName();
        }
        return owner.getName().getUniqueName() + "?" + getPropertiesWithValue().entrySet().stream()
                .map(entry -> entry.getKey().getPropertyName()+"="+entry.getKey().valueToString(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    public BlockState fromStorageString(String s){
        var strings = s.split("\\?");
        if(strings.length > 2){
            throw new IllegalArgumentException(s);
        }
        if(!getPrototype().getName().getUniqueName().equals(strings[0])){
            throw new IllegalArgumentException(getPrototype().getName() + " not containing " + s);
        }
        if(strings.length == 1){
            return this;
        }
        final BlockState[] state = {this};
        Arrays.stream(strings[1].split("&")).forEach(kv -> {
            var split = kv.split("=");
            Property property = state[0].getProperty(split[0]).get();
            state[0] = state[0].with(property, (Comparable) property.stringToValue(split[1]).get());
        });
        return state[0];
    }

    public static String getBlockNameFromStorageString(String s){
        var strings = s.split("\\?");
        return strings[0];
    }

}
