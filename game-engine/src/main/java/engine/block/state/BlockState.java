package engine.block.state;

import engine.block.Block;
import engine.state.Property;
import engine.state.State;

import java.util.Map;

public class BlockState extends State<Block, BlockState> {

    public BlockState(Block owner, Map<Property, Comparable> properties) {
        super(owner, properties);
    }

}
