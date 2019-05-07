package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.component.CheckBox;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;

public class CheckBoxRenderer implements ComponentRenderer<CheckBox> {
    @Override
    public void render(CheckBox component, Graphics graphics) {
        component.background().getValue().render(component, graphics);
        component.border().getValue().render(component, graphics);
        if (component.state().getValue() == null || component.state().getValue()) {
            Insets insets = component.padding().getValue();
            float width;
            float height;
            if(component.state().getValue() == null) {
                width = component.width().get() - insets.getRight() - insets.getLeft();
                height = component.width().get() - insets.getTop() - insets.getBottom();
                graphics.pushClipRect(insets.getLeft(), insets.getTop(), width, height);
                graphics.setColor(component.contentColor().getValue());
                graphics.fillRect(0, 0, width, height);
                graphics.popClipRect();
            }
            else{
                width = component.width().get();
                height = component.width().get();
                float vc = (float)Math.sqrt(Math.pow(component.border().getValue().getInsets().getLeft(),2) / 2);
                Tessellator tessellator = Tessellator.getInstance();
                GLBuffer buffer = tessellator.getBuffer();
                buffer.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA);
                buffer.pos(vc,0,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(0,vc,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(width,height - vc,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(width - vc,height,0).color(component.contentColor().getValue()).endVertex();
                tessellator.draw();
                buffer.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA);
                buffer.pos(0,height - vc,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(vc,height,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(width - vc,0,0).color(component.contentColor().getValue()).endVertex();
                buffer.pos(width,vc,0).color(component.contentColor().getValue()).endVertex();
                tessellator.draw();
            }
        }
    }
}
