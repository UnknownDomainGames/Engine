package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;
import engine.gui.text.Text;

public class Example1 extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button1 = new Button("Sinister");
        Button button2 = new Button("Dexter");
        Button button3 = new Button("Medium");
        Text text = new Text();

        button1.setOnAction(event -> text.text().set("Left"));
        button2.setOnAction(event -> text.text().set("Right"));
        button3.setOnAction(event -> text.text().set("Center"));

        VBox vBox = new VBox();
        vBox.spacing().set(10);
        vBox.getChildren().addAll(button1, button2, button3, text);
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);
        flowPane.getSize().setPrefSize(300, 300);
        flowPane.getChildren().add(vBox);

        Scene scene = new Scene(flowPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
