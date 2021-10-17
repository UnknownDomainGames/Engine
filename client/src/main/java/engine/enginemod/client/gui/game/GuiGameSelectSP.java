package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.EngineClientImpl;
import engine.client.i18n.I18n;
import engine.game.GameData;
import engine.game.GameDataStorage;
import engine.gui.Scene;
import engine.gui.control.*;
import engine.gui.input.MouseActionEvent;
import engine.gui.layout.BorderPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.*;
import engine.gui.stage.Popup;
import engine.util.Color;

import java.nio.file.Path;

public class GuiGameSelectSP extends BorderPane {
    public GuiGameSelectSP() {
        this(Platform.getEngineClient().getRunPath().resolve("game"));
    }

    public GuiGameSelectSP(Path gameStorageBasePath) {
        var storage = new GameDataStorage(gameStorageBasePath);

        setBackground(new Background(Color.fromRGB(0x666666)));
        setPadding(new Insets(5));

        { // Title
            var text = new Label(I18n.translate("engine.gui.game_select_single_player.title"));
            text.setFontSize(24);
            var wrapper = new VBox();
            wrapper.alignment().set(HPos.CENTER);
            wrapper.getChildren().add(text);
            top().set(wrapper);
        }

        ListView<GameData> list;
        { // Game List
            list = new ListView<>();
            list.cellFactory().set(listView -> new ListCell<>() {
                @Override
                protected void updateItem(GameData item, boolean empty) {
                    super.updateItem(item, empty);
                    var layout = new VBox();
                    var title = new Label("[PH] GameData");
                    title.setFontSize(16);
                    var desc = new Label("Nothing is here in this dimension...");
                    desc.color().set(Color.fromGray(0.67f));
                    if (!empty) {
                        title.text().set(item.getName());
                        desc.text().set(String.format("%d worlds, %d dependencies", item.getWorlds().size(), item.getDependencies().size()));
                    }
                    layout.getChildren().addAll(title, desc);
                    setGraphic(layout);
                }
            });
            list.items().addAll(storage.getGames().values());
//          list.getSize().setPrefSize(Size.USE_PARENT_VALUE,Size.USE_PARENT_VALUE);
            center().set(list);
        }

        { // Actions
            var layout = new HBox();
            layout.alignment().set(VPos.CENTER);
            layout.setSpacing(5);

            Button buttonPlay;
            { // Play
                buttonPlay = new Button(I18n.translate("engine.gui.game_select_single_player.play"));
                buttonPlay.setOnAction(event -> {
                    var engine = Platform.getEngineClient();
                    ((EngineClientImpl) engine).playIntegratedGame(list.selectionModel().get().selectedItem().get().getName());
                });
                layout.getChildren().add(buttonPlay);
            }

            { // Create
                var button = new Button(I18n.translate("engine.gui.create"));
                button.setOnAction(event -> Platform.getEngineClient().getGraphicsManager().getGUIManager()
                        .show(new Scene(new GuiCreateWorld(storage))));
                layout.getChildren().add(button);
            }

            Button buttonDelete;
            { // Delete
                buttonDelete = new Button(I18n.translate("engine.gui.delete"));
                buttonDelete.setOnAction(event -> {

                    var popup = new Popup();
                    VBox vBox = new VBox();
                    vBox.alignment().set(HPos.CENTER);
                    vBox.setBorder(new Border(Color.WHITE));
                    Text text = new Text(I18n.translate("engine.gui.warning"));
                    Text info = new Text(I18n.translate("engine.gui.game_select_single_player.delete.warning.info"));

                    var wrapping = new VBox();
                    var lbl3 = new Label("[PH] GameData");
                    lbl3.setFontSize(16);
                    var desc = new Label("Nothing is here in this dimension...");
                    desc.color().set(Color.fromGray(0.67f));
                    wrapping.getChildren().addAll(lbl3, desc);
                    var data = list.selectionModel().get().selectedItem().get();
                    if (data != null) {
                        lbl3.text().set(data.getName());
                        desc.text().set(String.format("%d worlds, %d dependencies", data.getWorlds().size(), data.getDependencies().size()));
                    }
                    wrapping.border().set(new Border(Color.WHITE, 2));

                    HBox func = new HBox();
                    Button butConfirm = new Button(I18n.translate("engine.gui.confirm"));
                    Button butCancel = new Button(I18n.translate("engine.gui.cancel"));
                    func.getChildren().addAll(butConfirm, butCancel);
                    butCancel.setOnAction(event1 -> popup.hide());
                    vBox.getChildren().add(text);
                    vBox.getChildren().add(info);
                    vBox.getChildren().add(wrapping);
                    vBox.getChildren().add(func);

                    popup.setScene(new Scene(vBox));
                    popup.setAutoFix(true);
                    var stage = GuiGameSelectSP.this.getScene().getStage();
                    popup.show(stage, stage.getX() + stage.getWidth() / 2f, stage.getY() + stage.getHeight() / 2f);
                });
                layout.getChildren().add(buttonDelete);
            }

            { // Play and Delete disable
                list.selectionModel().get().selectedIndex().addChangeListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue != -1) {
                        buttonPlay.setDisabled(false);
                        buttonDelete.setDisabled(false);
                    } else {
                        buttonPlay.setDisabled(true);
                        buttonDelete.setDisabled(true);
                    }
                });

                buttonPlay.setDisabled(true);
                buttonPlay.setDisabled(true);
            }

            { // Back
                var button = new Button(I18n.translate("engine.gui.back"));
                button.setOnAction(event -> Platform.getEngineClient().getGraphicsManager().getGUIManager().showLast());
                layout.getChildren().add(button);
            }

            bottom().set(layout);
        }
    }
}
