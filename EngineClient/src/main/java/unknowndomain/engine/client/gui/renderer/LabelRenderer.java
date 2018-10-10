package unknowndomain.engine.client.gui.renderer;

import unknowndomain.engine.client.gui.Graphics;
import unknowndomain.engine.client.gui.component.Label;

public class LabelRenderer extends ComponentRenderer<Label> {

    public LabelRenderer(Label component) {
        super(component);
    }

    @Override
    public void render(Graphics graphics) {
        graphics.drawText(getComponent().getText(), 0, 0);
    }
}
