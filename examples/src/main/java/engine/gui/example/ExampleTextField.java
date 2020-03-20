package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.TextField;
import engine.gui.layout.FlowPane;
import engine.gui.misc.Border;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;
import engine.util.Color;

public class ExampleTextField extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        TextField textField = new TextField();
        textField.setBorder(new Border(Color.WHITE));
        textField.promptText().set("Enter...");
        textField.getSize().setPrefSize(200, 24);

        flowPane.getChildren().add(textField);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
