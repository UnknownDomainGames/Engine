package unknowndomain.engine.client.gui.component;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Container;

//TODO: everything that can communicate with program and user should extends 'Control'
public class Button extends Container {

    private Component content;

    public Component getContent(){
        return content;
    }

    public void setContent(Component content){
        //TODO: event
        this.content = content;
    }

    public void setContent(String text){
        setContent(new Label(text));
    }

    //TODO: action performed

}
