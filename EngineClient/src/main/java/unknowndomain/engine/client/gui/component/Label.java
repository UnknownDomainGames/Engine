package unknowndomain.engine.client.gui.component;

import unknowndomain.engine.client.gui.Component;

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
}
