package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.i18n.I18n;
import engine.client.i18n.LocaleDefinition;
import engine.client.i18n.LocaleManager;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Label;
import engine.gui.control.ListCell;
import engine.gui.control.ListView;
import engine.gui.layout.BorderPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.HPos;
import engine.gui.misc.Insets;
import engine.util.Color;

public class GuiLanguageList extends BorderPane {

    public GuiLanguageList() {
        setBackground(new Background(Color.fromRGB(0x666666)));
        padding().set(new Insets(5));
        { // Title
            var text = new Label(I18n.translate("engine.gui.settings.language.title"));
            text.setFontSize(24);
            var wrapper = new VBox();
            wrapper.alignment().set(HPos.CENTER);
            wrapper.getChildren().add(text);
            top().set(wrapper);
        }

        ListView<LocaleDefinition> list;
        { // List
            list = new ListView<>();
            list.cellFactory().set(listView -> new ListCell<>() {
                @Override
                protected void updateItem(LocaleDefinition item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(item.getName() + " (" + item.getRegion() + ")");
                    } else {
                        setText("");
                    }
                }
            });
            list.items().addAll(LocaleManager.INSTANCE.getLocaleDefinitions().values());
            list.selectionModel().get().select(LocaleManager.INSTANCE.getCurrentLocale());
            center().set(list);
        }

        { // Actions
            var layout = new HBox();
            { // Confirm
                var button = new Button(I18n.translate("engine.gui.confirm"));
                button.setOnAction(event -> {
                    var currentSelection = list.selectionModel().get().selectedItem().get();
                    if (!LocaleManager.INSTANCE.getCurrentLocale().equals(currentSelection)) {
                        // Locale changed
                        LocaleManager.INSTANCE.setLocale(currentSelection);
                        var settings = Platform.getEngineClient().getSettings();
                        settings.setLanguage(currentSelection.getCode());
                        settings.save();
                    }
                    { // Reload assets
                        Platform.getEngineClient().getAssetManager().reload();
                    }
                    { // Close window
                        var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
                        guiManager.close();
                        guiManager.show(new Scene(new GuiMainMenu()));
                    }
                });
                layout.getChildren().add(button);
            }
            { // Cancel
                var button = new Button(I18n.translate("engine.gui.cancel"));
                button.setOnAction(event -> {
                    var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
                    guiManager.close();
                    guiManager.show(new Scene(new GuiMainMenu()));
                });
                layout.getChildren().add(button);
            }
            bottom().set(layout);
        }
    }
}
