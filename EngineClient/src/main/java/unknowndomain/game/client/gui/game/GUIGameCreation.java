package unknowndomain.game.client.gui.game;

import unknowndomain.engine.Platform;
import unknowndomain.engine.I18n.I18n;
import unknowndomain.engine.I18n.LocaleManager;
import unknowndomain.engine.I18n.Locales;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.gui.component.*;
import unknowndomain.engine.client.gui.layout.BorderPane;
import unknowndomain.engine.client.gui.layout.VBox;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.player.PlayerImpl;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.util.Color;

import java.util.UUID;

public class GUIGameCreation extends BorderPane {

	public GUIGameCreation() {
		VBox vBox = new VBox();
		vBox.spacing().set(5);
		center().setValue(vBox);
		this.background().setValue(new Background(Color.fromRGB(0xAAAAAA)));
		vBox.padding().setValue(new Insets(100, 350, 0, 350));

		Label text = new Label();
		text.text().setValue(I18n.translation("engine.gui.game_creation.text.name"));
		text.font().setValue(new Font(Font.getDefaultFont(), 20));
		vBox.getChildren().add(text);

		Button buttonCreate = new Button("Create");
		buttonCreate.border().setValue(new Border(Color.WHITE));

		buttonCreate.setOnClick(mouseClickEvent -> {
			var engine = Platform.getEngineClient();
			var player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
			engine.getRenderContext().getGuiManager().closeScreen();
			engine.startGame(new GameClientStandalone(engine, player));

		});
		vBox.getChildren().add(buttonCreate);

		Button buttonExit = new Button("exit");
		buttonExit.disabled().set(true);
		vBox.getChildren().add(buttonExit);

		HSlider v = new HSlider();
		v.resizeBack(300,20);
		v.backBg().setValue(new Background(Color.BLUE));
		v.sliderBg().setValue(new Background(Color.WHITE));
		v.resizeSlider(8,20);
		v.setPreMove(0.02);
		vBox.getChildren().addAll(v);
		
		Button buttonLocale = new Button("Lang: "+I18n.translation("engine.gui.lang.text.name"));
		buttonLocale.setOnClick(onClick -> {
			if (LocaleManager.localeManager.getLocale() == Locales.en_us) {
				LocaleManager.localeManager.setLocale(Locales.zh_cn);
			} else if (LocaleManager.localeManager.getLocale() == Locales.zh_cn) {
				LocaleManager.localeManager.setLocale(Locales.en_us);
			}
			buttonLocale.text().setValue("Lang: "+I18n.translation("engine.gui.lang.text.name"));
		});
		vBox.getChildren().add(buttonLocale);

	}

}
