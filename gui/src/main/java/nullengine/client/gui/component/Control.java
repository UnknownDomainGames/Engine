package nullengine.client.gui.component;

import com.google.common.collect.Lists;
import nullengine.client.gui.Node;
import nullengine.client.gui.Region;

import java.util.List;

public abstract class Control extends Region {

    @Override
    public boolean isResizable() {
        return true;
    }

//    @Override
//    public List<Node> getPointingComponents(float posX, float posY) {
//        return Lists.newArrayList(this);
//    }
}
