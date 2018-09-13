package unknowndomain.engine.client.gui;

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
