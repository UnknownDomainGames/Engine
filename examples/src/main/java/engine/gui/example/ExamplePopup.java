package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Border;
import engine.gui.misc.Bounds;
import engine.gui.misc.Pos;
import engine.gui.stage.Popup;
import engine.gui.stage.Stage;
import engine.gui.text.Text;
import engine.util.Color;

import java.lang.management.ManagementFactory;

public class ExamplePopup extends GUIApplication {
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.spacing().set(5);

        Button showPopup = new Button("Show Popup");
        showPopup.setBorder(new Border(Color.WHITE));
        showPopup.setOnAction(event -> {
            Popup popup = new Popup();

            VBox vBox = new VBox();
            vBox.alignment().set(Pos.HPos.CENTER);
            vBox.setBorder(new Border(Color.WHITE));
            Text text = new Text("Hello Popup");
            Button hidePopup = new Button("Hide Popup");
            hidePopup.setOnAction(event1 -> popup.hide());
            vBox.getChildren().addAll(text, hidePopup);

            Bounds boundsInScreen = showPopup.getBoundsInScreen();
            popup.setScene(new Scene(vBox));
            popup.setAutoHide(true);
            popup.show(showPopup, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
        });

        hBox.getChildren().addAll(showPopup);

        flowPane.getChildren().add(hBox);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Example Popup");
        primaryStage.show();
    }
}
