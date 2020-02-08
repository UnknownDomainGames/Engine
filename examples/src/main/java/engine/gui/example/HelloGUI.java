package engine.gui.example;

import engine.gui.Scene;
import engine.gui.Stage;
import engine.gui.application.GUIApplication;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Border;
import engine.gui.misc.Pos;
import engine.gui.text.Text;
import engine.util.Color;

public class HelloGUI extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.alignment().set(Pos.HPos.CENTER);
        vBox.spacing().set(5);

        Text text = new Text("Hello World");

        Button sayIt = new Button("Say it");
        sayIt.border().set(new Border(Color.WHITE));
        sayIt.setOnAction(event -> System.out.println("Hello World"));

        vBox.getChildren().addAll(text, sayIt);

        flowPane.getChildren().add(vBox);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
