package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.component.Button;

public class ButtonRenderer implements ComponentRenderer {

    public static final ButtonRenderer INSTANCE = new ButtonRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        if(component instanceof Button){
            Button button = (Button)component;
            if(button.disabled().get()){
                button.disabledbackground().getValue().render(component, graphics);
            }
            else if(button.pressed().get()){
                button.pressbackground().getValue().render(component, graphics);
            }else if(button.hover().get()){
                button.hoverbackground().getValue().render(component, graphics);
            }else{
                button.background().getValue().render(component, graphics);
            }
            graphics.fillRect(0,0,button.prefWidth(),button.prefHeight());
            graphics.pushClipRect(button.getCachedText().x().get(), button.getCachedText().y().get(), button.getCachedText().width().get(), button.getCachedText().height().get());
            button.getCachedText().getRenderer().render(button.getCachedText(),graphics);
            graphics.popClipRect();
        }
    }
}
