package unknowndomain.engine.client.gui.component;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.renderer.ComponentRenderer;
import unknowndomain.engine.client.gui.renderer.LabelRenderer;

public class Label extends Component {

    private String text;

    public Label(String content){
        setText(content);
    }

    public void setText(String text){
        //TODO: event
        this.text = text;
    }

    public String getText(){
        return text;
    }

    @Override
    public int prefWidth() {
        return 0;
    }

    @Override
    public int prefHeight() {
        return 0;
    }

    @Override
    protected ComponentRenderer<?> createDefaultRenderer() {
        return new LabelRenderer(this);
    }
}
