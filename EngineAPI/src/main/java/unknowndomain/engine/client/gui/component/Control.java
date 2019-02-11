package unknowndomain.engine.client.gui.component;

import com.google.common.collect.Lists;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Region;

import java.util.List;

public class Control extends Region {
    @Override
    public List<Component> getPointingComponents(float posX, float posY) {
        return Lists.newArrayList(this);
    }
}
