package engine.enginemod.client.gui.settings;

import engine.Platform;
import engine.client.i18n.I18n;
import engine.enginemod.client.gui.game.GuiMainMenu;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.gui.Scene;
import engine.gui.control.*;
import engine.gui.layout.BorderPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.HPos;
import engine.gui.misc.Insets;
import engine.util.Color;

public class GuiFontList extends BorderPane {

    public GuiFontList() {
        setBackground(new Background(Color.fromRGB(0x666666)));
        padding().set(new Insets(5));
        var settings = Platform.getEngineClient().getSettings();
        var preview = new TextField();
        { // Title
            var text = new Label(I18n.translate("engine.gui.settings.font.title"));
            text.setFontSize(24);
            var wrapper = new VBox();
            wrapper.alignment().set(HPos.CENTER);
            wrapper.getChildren().add(text);
            top().set(wrapper);
        }

        ListView<Font> list;
        { // List
            list = new ListView<>();
            list.cellFactory().set(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Font item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        var fullName = item.getFullName();
                        if (fullName.equals(item.getName())) {
                            setText(fullName);
                        } else {
                            setText(fullName + " (" + item.getName() + ")");
                        }
                        setFont(item.withSize(FontManager.instance().getDefaultFont().getSize()));
                    } else {
                        setText("");
                    }
                }
            });
            list.items().addAll(FontManager.instance().getAvailableFonts());
            list.selectionModel().get().selectedItem().addChangeListener((observable, oldValue, newValue) ->
                    preview.font().set(newValue.withSize(preview.font().get().getSize())));
            list.selectionModel().get().select(FontManager.instance().getDefaultFont().withSize(1));
            var scroll = new ScrollPane();
            scroll.setContent(list);
            center().set(scroll);
        }

        { // Bottom
            var bottom = new VBox();
            { // Size
                var size = new HBox();
                var label = new Text(I18n.translate("engine.gui.settings.font.size"));
                var slider = new HSlider();
                slider.max().set(50);
                slider.min().set(1);
                slider.step().set(0.5f);
                slider.value().addChangeListener((observable, oldValue, newValue) ->
                        preview.font().set(preview.font().get().withSize(newValue)));
                var value = new Text();
                slider.value().addChangeListener((observable, oldValue, newValue) -> value.setText(newValue.toString()));
                slider.value().set(FontManager.instance().getDefaultFont().getSize());
                size.getChildren().addAll(label, slider, value);
                bottom.getChildren().add(size);
            }
            { // Preview
                preview.promptText().set(I18n.translate("engine.gui.settings.font.default_preview"));
                var layout = new HBox();
                layout.getChildren().add(preview);
                bottom.getChildren().add(layout);
            }
            { // Actions
                var layout = new HBox();
                { // Confirm
                    var button = new Button(I18n.translate("engine.gui.confirm"));
                    button.setOnAction(event -> {
                        var manager = FontManager.instance();
                        var currentSelection = list.selectionModel().get().selectedItem().get().withSize(manager.getDefaultFont().getSize());
                        if (!manager.getDefaultFont().equals(currentSelection)) {
                            // Font changed
                            manager.setDefaultFont(currentSelection);
                            settings.getDisplaySettings().setFont(currentSelection);
                            settings.save();
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
                { // Reset
                    var button = new Button(I18n.translate("engine.gui.reset"));
                    button.setOnAction(event -> {
                        FontManager.instance().setDefaultFont(null);
                        settings.getDisplaySettings().setFont(null);
                        settings.save();
                        var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
                        guiManager.close();
                        guiManager.show(new Scene(new GuiMainMenu()));
                    });
                    layout.getChildren().add(button);
                }
                bottom.getChildren().add(layout);
            }
            bottom().set(bottom);
        }
    }
}
