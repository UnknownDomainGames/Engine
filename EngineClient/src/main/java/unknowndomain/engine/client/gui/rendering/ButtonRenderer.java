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
                graphics.setColor(button.disabledbackground().getValue());
            }
            else if(button.pressed().get()){
                graphics.setColor(button.pressbackground().getValue());
            }else if(button.hover().get()){
                graphics.setColor(button.hoverbackground().getValue());
            }else{
                graphics.setColor(button.background().getValue());
            }
            graphics.fillRect(0,0,button.prefWidth(),button.prefHeight());
            button.getCachedText().getRenderer().render(button.getCachedText(),graphics);
        }
    }
}
