package engine.enginemod.client.gui;

import com.github.mouse0w0.observable.value.ValueChangeListener;
import engine.Platform;
import engine.client.settings.EngineSettings;
import engine.graphics.display.DisplayMode;
import engine.graphics.font.Font;
import engine.gui.control.Button;
import engine.gui.control.HSlider;
import engine.gui.control.Text;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.input.MouseButton;
import engine.util.Color;

public class GuiSettings extends AnchorPane {
    private int videoModeIndex;

    public GuiSettings() {
        setBackground(new Background(Color.fromRGB(0xAAAAAA)));
        var settings = Platform.getEngineClient().getSettings();
        var baksettings = new EngineSettings();
        baksettings.getDisplaySettings().setDisplayMode(settings.getDisplaySettings().getDisplayMode());
        baksettings.getDisplaySettings().setResolutionHeight(settings.getDisplaySettings().getResolutionHeight());
        baksettings.getDisplaySettings().setResolutionWidth(settings.getDisplaySettings().getResolutionWidth());
        baksettings.getDisplaySettings().setUiScale(settings.getDisplaySettings().getUiScale());
        baksettings.getDisplaySettings().setHudScale(settings.getDisplaySettings().getHudScale());
        var title = new Text("Engine Settings");
        title.setFont(new Font(Font.getDefaultFont(), 20.0f));
        //Display settings
        var lblDisplayMode = new Text("Display Mode");
        var lblRes = new Text("Resolution");
        var butRes = new Button();
        var screen = Platform.getEngineClient().getGraphicsManager().getWindow().getScreen();
        screen.
                getVideoModes().stream().filter(mode -> mode.getWidth() == (settings.getDisplaySettings().getResolutionWidth() == -1 ? screen.getVideoMode().getWidth() : settings.getDisplaySettings().getResolutionWidth())
                && mode.getHeight() == (settings.getDisplaySettings().getResolutionHeight() == -1 ? screen.getVideoMode().getHeight() : settings.getDisplaySettings().getResolutionHeight())
                && mode.getRefreshRate() == (settings.getDisplaySettings().getFrameRate() == -1 ? screen.getVideoMode().getRefreshRate() : settings.getDisplaySettings().getFrameRate()))
                .findFirst().ifPresentOrElse(videoMode -> videoModeIndex = screen.getVideoModes().indexOf(videoMode),
                () -> videoModeIndex = screen.getVideoModes().indexOf(screen.getVideoMode()));
        butRes.text().set(String.format("%dx%d, %dHz", Platform.getEngineClient().getGraphicsManager().getWindow().getWidth(), Platform.getEngineClient().getGraphicsManager().getWindow().getHeight(), settings.getDisplaySettings().getDisplayMode() != DisplayMode.FULLSCREEN ? 60 : screen.getVideoMode().getRefreshRate()));
        butRes.setOnMouseClicked(event -> {
            videoModeIndex = (GuiSettings.this.videoModeIndex + 1) % screen.getVideoModes().size();
            settings.getDisplaySettings().setResolutionWidth(screen.getVideoModes().get(videoModeIndex).getWidth());
            settings.getDisplaySettings().setResolutionHeight(screen.getVideoModes().get(videoModeIndex).getHeight());
            settings.getDisplaySettings().setFrameRate(screen.getVideoModes().get(videoModeIndex).getRefreshRate());
            butRes.text().set(String.format("%dx%d, %dHz", settings.getDisplaySettings().getResolutionWidth(), settings.getDisplaySettings().getResolutionHeight(), settings.getDisplaySettings().getFrameRate()));
        });
        butRes.setDisabled(settings.getDisplaySettings().getDisplayMode() != DisplayMode.FULLSCREEN);
        var butDisplayMode = new Button();
        butDisplayMode.text().set(settings.getDisplaySettings().getDisplayMode().name());
        butDisplayMode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.MOUSE_BUTTON_PRIMARY) {
                settings.getDisplaySettings().setDisplayMode(DisplayMode.values()[(settings.getDisplaySettings().getDisplayMode().ordinal() + 1) % DisplayMode.values().length]);
                butDisplayMode.text().set(settings.getDisplaySettings().getDisplayMode().name());
                if (settings.getDisplaySettings().getDisplayMode() == DisplayMode.FULLSCREEN) {
                    butRes.setDisabled(false);
                    if (settings.getDisplaySettings().getResolutionWidth() == -1 || settings.getDisplaySettings().getResolutionHeight() == -1 || settings.getDisplaySettings().getFrameRate() == -1) {
                        settings.getDisplaySettings().setResolutionWidth(screen.getVideoMode().getWidth());
                        settings.getDisplaySettings().setResolutionHeight(screen.getVideoMode().getHeight());
                        settings.getDisplaySettings().setFrameRate(screen.getVideoMode().getRefreshRate());
                    }
                    butRes.text().set(String.format("%dx%d, %dHz", settings.getDisplaySettings().getResolutionWidth(), settings.getDisplaySettings().getResolutionHeight(), settings.getDisplaySettings().getFrameRate()));
                } else {
                    butRes.setDisabled(true);
                    settings.getDisplaySettings().setResolutionWidth(settings.getDisplaySettings().getDisplayMode() == DisplayMode.WINDOWED ? -1 : screen.getVideoMode().getWidth());
                    settings.getDisplaySettings().setResolutionHeight(settings.getDisplaySettings().getDisplayMode() == DisplayMode.WINDOWED ? -1 : screen.getVideoMode().getHeight());
                    settings.getDisplaySettings().setFrameRate(60);
                    butRes.text().set(String.format("%dx%d, %dHz", Platform.getEngineClient().getGraphicsManager().getWindow().getWidth(), Platform.getEngineClient().getGraphicsManager().getWindow().getHeight(), 60));
                }
            }
        });
        var lblUiScale = new Text("UI Scale");
        var sliderUiScale = new HSlider();
        sliderUiScale.max().set(200);
        sliderUiScale.min().set(50);
        sliderUiScale.step().set(1.0f);
        sliderUiScale.sliderLength().set(250);
        sliderUiScale.value().set(settings.getDisplaySettings().getUiScale());
        var lblUiScaleVal = new Text(String.valueOf(settings.getDisplaySettings().getUiScale()));
        sliderUiScale.value().addChangeListener((observable, oldValue, newValue) -> {
            var i = (int) Math.round(sliderUiScale.value().get());
            lblUiScaleVal.setText(String.valueOf(i));
            settings.getDisplaySettings().setUiScale(i);
        });
        var lblHudScale = new Text("HUD Scale");
        var sliderHudScale = new HSlider();
        sliderHudScale.max().set(200);
        sliderHudScale.min().set(50);
        sliderHudScale.step().set(1.0f);
        sliderHudScale.sliderLength().set(250);
        sliderHudScale.value().set(settings.getDisplaySettings().getHudScale());
        var lblHudScaleVal = new Text(String.valueOf(settings.getDisplaySettings().getHudScale()));
        sliderHudScale.value().addChangeListener((observable, oldValue, newValue) -> {
            var i = (int) Math.round(sliderHudScale.value().get());
            lblHudScaleVal.setText(String.valueOf(i));
            settings.getDisplaySettings().setHudScale(i);
        });
        var hb1 = new HBox();
        hb1.spacing().set(10f);
        hb1.getChildren().addAll(lblDisplayMode, butDisplayMode);
        var hb2 = new HBox();
        hb2.spacing().set(10f);
        hb2.getChildren().addAll(lblRes, butRes);
        var hb3 = new HBox();
        hb3.spacing().set(10f);
        hb3.getChildren().addAll(lblUiScale, sliderUiScale, lblUiScaleVal);
        var hb4 = new HBox();
        hb4.spacing().set(10f);
        hb4.getChildren().addAll(lblHudScale, sliderHudScale, lblHudScaleVal);
        var vb = new VBox();
        vb.getChildren().addAll(hb1, hb2, hb3, hb4);

        //All
        setTopAnchor(title, 10f);
        setLeftAnchor(title, (this.getWidth() - title.getWidth()) / 2);
        setTopAnchor(vb, title.getHeight() + 20f);
        setLeftAnchor(vb, (this.getWidth() - vb.getWidth()) / 2);
        var butBack = new Button("Return without Saving");
        var butSave = new Button("Save and Return");
        butBack.setMinSize(25, 25);
        butBack.setOnMouseClicked(event -> {
            settings.getDisplaySettings().setDisplayMode(baksettings.getDisplaySettings().getDisplayMode());
            settings.getDisplaySettings().setResolutionHeight(baksettings.getDisplaySettings().getResolutionHeight());
            settings.getDisplaySettings().setResolutionWidth(baksettings.getDisplaySettings().getResolutionWidth());
            settings.getDisplaySettings().setUiScale(baksettings.getDisplaySettings().getUiScale());
            settings.getDisplaySettings().setHudScale(baksettings.getDisplaySettings().getHudScale());
            var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
            guiManager.showLast();
        });
        butSave.setOnMouseClicked(event -> {
            settings.apply();
            settings.save();
            var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
            guiManager.showLast();
        });
        butSave.setBorder(new Border(Color.WHITE, 2));
        butBack.setBorder(new Border(Color.WHITE, 2));
        var hb5 = new HBox();
        hb5.getChildren().addAll(butSave, butBack);
        setBottomAnchor(hb5, 10f);
        setBottomAnchor(vb, hb5.getHeight() + 20f);
        setRightAnchor(hb5, 10f);
        this.getChildren().addAll(title, vb, hb5);
        ValueChangeListener<Float> sizeChangeListener = (observable, oldValue, newValue) -> {
            setTopAnchor(title, 10f);
            setLeftAnchor(title, (this.getWidth() - title.getWidth()) / 2);
            setTopAnchor(vb, title.getHeight() + 20f);
            setLeftAnchor(vb, (this.getWidth() - vb.getWidth()) / 2);
            setBottomAnchor(hb5, 10f);
            setBottomAnchor(vb, hb5.getHeight() + 20f);
            setRightAnchor(hb5, 10f);
            layoutChildren();
        };
        width().addChangeListener(sizeChangeListener);
        height().addChangeListener(sizeChangeListener);
    }
}
