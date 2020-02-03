package nullengine.client.gui.control;

import nullengine.client.gui.Region;

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
