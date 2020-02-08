package engine.block;

public class AirBlock extends BaseBlock {

    public static final Block AIR = new AirBlock();

    public AirBlock() {
        setShape(BlockShape.EMPTY);
        name("air");
    }
}
