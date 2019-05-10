package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.component.Label;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.text.Text;

public class LabelRenderer extends RegionRenderer<Label>  {
    public static final LabelRenderer INSTANCE = new LabelRenderer();
    @Override
    public void render(Label label, Graphics graphics) {
        super.render(label,graphics);
        label.getCachedText().getRenderer().render(label.getCachedText(),graphics);
    }
}
