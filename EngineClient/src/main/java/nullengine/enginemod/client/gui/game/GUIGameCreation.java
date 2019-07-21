package nullengine.enginemod.client.gui.game;

import nullengine.Platform;
import nullengine.client.game.GameClientStandalone;
import nullengine.client.gui.component.Button;
import nullengine.client.gui.component.Label;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.i18n.I18n;
import nullengine.client.i18n.LocaleManager;
import nullengine.client.rendering.font.Font;
import nullengine.player.PlayerImpl;
import nullengine.player.Profile;
import nullengine.util.Color;

import java.util.Locale;
import java.util.UUID;

public class GUIGameCreation extends BorderPane {

    public GUIGameCreation() {
        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().setValue(Pos.HPos.CENTER);
        center().setValue(vBox);
        this.background().setValue(new Background(Color.fromRGB(0xAAAAAA)));
        vBox.padding().setValue(new Insets(100, 0, 0, 0));

        Label text = new Label();
        text.text().setValue(I18n.translate("engine.gui.game_creation.text.name"));
        text.font().setValue(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button buttonCreate = new Button("New World");
        buttonCreate.border().setValue(new Border(Color.WHITE));

        buttonCreate.setOnClick(mouseClickEvent -> {
            var engine = Platform.getEngineClient();
            var player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
            engine.getRenderContext().getGuiManager().closeScreen();
            engine.startGame(new GameClientStandalone(engine, engine.getRunPath().resolve("game"), player));
        });
        vBox.getChildren().add(buttonCreate);

        Button buttonExit = new Button("exit");
        buttonExit.setOnClick(mouseClickEvent -> Platform.getEngine().terminate());
        vBox.getChildren().add(buttonExit);

        Button buttonLocale = new Button("Lang: " + I18n.translate("engine.gui.lang.text.name"));
        buttonLocale.setOnClick(onClick -> {
            if (LocaleManager.INSTANCE.getLocale() == Locale.US) {
                LocaleManager.INSTANCE.setLocale(Locale.CHINA);
            } else if (LocaleManager.INSTANCE.getLocale() == Locale.CHINA) {
                LocaleManager.INSTANCE.setLocale(Locale.US);
            }
            buttonLocale.text().setValue("Lang: " + I18n.translate("engine.gui.lang.text.name"));
        });
        vBox.getChildren().add(buttonLocale);

    }


    @Override
    public void requireClose() {
        // Main Menu should not be required close
    }
}
