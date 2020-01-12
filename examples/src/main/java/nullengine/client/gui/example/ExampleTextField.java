package nullengine.client.gui.example;

import nullengine.client.gui.Scene;
import nullengine.client.gui.Stage;
import nullengine.client.gui.application.GUIApplication;
import nullengine.client.gui.component.TextField;
import nullengine.client.gui.layout.FlowPane;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Pos;
import nullengine.util.Color;

public class ExampleTextField extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        TextField textField = new TextField();
        textField.border().set(new Border(Color.WHITE));
        textField.getSize().setPrefSize(200, 24);

        flowPane.getChildren().add(textField);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
