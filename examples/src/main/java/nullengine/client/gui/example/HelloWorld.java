package nullengine.client.gui.example;

import nullengine.client.gui.Scene;
import nullengine.client.gui.Stage;
import nullengine.client.gui.application.GUIApplication;
import nullengine.client.gui.component.Button;
import nullengine.client.gui.layout.FlowPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.Text;
import nullengine.util.Color;

public class HelloWorld extends GUIApplication {
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
        sayIt.setOnMousePressed(event -> System.out.println("Hello World"));

        vBox.getChildren().addAll(text, sayIt);

        flowPane.getChildren().add(vBox);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
