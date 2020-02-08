package engine.component;

import java.nio.ByteBuffer;

public interface SerializableComponent<T extends SerializableComponent> extends Component {

    String serialKey();

    T load(ByteBuffer buffer); //TODO our own storage container like NBT?

    void save(ByteBuffer buffer); //TODO our own storage container like NBT?

}
