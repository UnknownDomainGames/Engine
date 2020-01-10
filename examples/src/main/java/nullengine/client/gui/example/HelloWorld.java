package nullengine.client.gui.example;

import nullengine.client.gui.Scene;
import nullengine.client.gui.application.GUIApplication;
import nullengine.client.gui.layout.FlowPane;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.display.Window;

public class HelloWorld extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Window primaryWindow) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);
        Text text = new Text("Hello World");
        flowPane.getChildren().add(text);
        Scene scene = new Scene(flowPane);
        scene.showToWindow(primaryWindow);
    }
}
