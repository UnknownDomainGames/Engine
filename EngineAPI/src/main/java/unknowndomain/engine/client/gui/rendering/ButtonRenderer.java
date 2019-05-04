package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.component.Button;

public class ButtonRenderer extends RegionRenderer<Button> {

    public static final ButtonRenderer INSTANCE = new ButtonRenderer();

    @Override
    public void render(Button button, Graphics graphics) {
        super.render(button,graphics);
        if(button != null){
//            if(button.disabled().get()){
//                button.disabledbackground().getValue().render(button, graphics);
//            }
//            else if(button.pressed().get()){
//                button.pressbackground().getValue().render(button, graphics);
//            }else if(button.hover().get()){
//                button.hoverbackground().getValue().render(button, graphics);
//            }else{
//                button.buttonbackground().getValue().render(button, graphics);
//            }
//            graphics.fillRect(0,0, button.prefWidth(), button.prefHeight());
            graphics.pushClipRect(button.getCachedText().x().get(), button.getCachedText().y().get(), button.getCachedText().width().get(), button.getCachedText().height().get());
            button.getCachedText().getRenderer().render(button.getCachedText(),graphics);
            graphics.popClipRect();
        }
    }
}
