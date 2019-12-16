package nullengine.enginemod.client.gui;

import com.github.mouse0w0.observable.value.ValueChangeListener;
import nullengine.Platform;
import nullengine.client.gui.GuiManager;
import nullengine.client.gui.component.Button;
import nullengine.client.gui.input.MouseButton;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.client.gui.layout.HBox;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.display.DisplayMode;
import nullengine.client.rendering.font.Font;
import nullengine.client.settings.EngineSettings;
import nullengine.util.Color;

public class GuiSettings extends AnchorPane {
    private int videoModeIndex;
    public GuiSettings(){
        var settings = Platform.getEngineClient().getSettings();
        var baksettings = new EngineSettings();
        baksettings.getDisplaySettings().setDisplayMode(settings.getDisplaySettings().getDisplayMode());
        baksettings.getDisplaySettings().setResolutionHeight(settings.getDisplaySettings().getResolutionHeight());
        baksettings.getDisplaySettings().setResolutionWidth(settings.getDisplaySettings().getResolutionWidth());

        var title = new Text("Engine Settings");
        title.font().setValue(new Font(Font.getDefaultFont(), 20.0f));
        //Display settings
        var lblDisplayMode = new Text("Display Mode");
        var lblRes = new Text("Resolution");
        var butRes = new Button();
        var monitor = Platform.getEngineClient().getRenderManager().getWindow().getMonitor();
        monitor.
                getVideoModes().stream().filter(mode -> mode.getWidth() == (settings.getDisplaySettings().getResolutionWidth() == -1 ? monitor.getVideoMode().getWidth() : settings.getDisplaySettings().getResolutionWidth())
        && mode.getHeight() == (settings.getDisplaySettings().getResolutionHeight() == -1 ? monitor.getVideoMode().getHeight() : settings.getDisplaySettings().getResolutionHeight())
        && mode.getRefreshRate() == (settings.getDisplaySettings().getFrameRate() == -1 ? monitor.getVideoMode().getRefreshRate() : settings.getDisplaySettings().getFrameRate()))
                .findFirst().ifPresentOrElse(videoMode -> videoModeIndex = monitor.getVideoModes().indexOf(videoMode),
                () -> videoModeIndex = monitor.getVideoModes().indexOf(monitor.getVideoMode()));
        butRes.text().setValue(String.format("%dx%d, %dHz", Platform.getEngineClient().getRenderManager().getWindow().getWidth(), Platform.getEngineClient().getRenderManager().getWindow().getHeight(), settings.getDisplaySettings().getDisplayMode() != DisplayMode.FULLSCREEN ? 60 : monitor.getVideoMode().getRefreshRate()));
        butRes.setOnMouseClicked(event -> {
            videoModeIndex = (GuiSettings.this.videoModeIndex + 1) % monitor.getVideoModes().size();
            settings.getDisplaySettings().setResolutionWidth(monitor.getVideoModes().get(videoModeIndex).getWidth());
            settings.getDisplaySettings().setResolutionHeight(monitor.getVideoModes().get(videoModeIndex).getHeight());
            settings.getDisplaySettings().setFrameRate(monitor.getVideoModes().get(videoModeIndex).getRefreshRate());
            butRes.text().setValue(String.format("%dx%d, %dHz", settings.getDisplaySettings().getResolutionWidth(), settings.getDisplaySettings().getResolutionHeight(), settings.getDisplaySettings().getFrameRate()));
        });
        butRes.disabled().set(settings.getDisplaySettings().getDisplayMode() != DisplayMode.FULLSCREEN);
        var butDisplayMode = new Button();
        butDisplayMode.text().setValue(settings.getDisplaySettings().getDisplayMode().name());
        butDisplayMode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.MOUSE_BUTTON_PRIMARY) {
                settings.getDisplaySettings().setDisplayMode(DisplayMode.values()[(settings.getDisplaySettings().getDisplayMode().ordinal() + 1) % DisplayMode.values().length]);
                butDisplayMode.text().setValue(settings.getDisplaySettings().getDisplayMode().name());
                if (settings.getDisplaySettings().getDisplayMode() == DisplayMode.FULLSCREEN) {
                    butRes.disabled().set(false);
                    if (settings.getDisplaySettings().getResolutionWidth() == -1 || settings.getDisplaySettings().getResolutionHeight() == -1 || settings.getDisplaySettings().getFrameRate() == -1) {
                        settings.getDisplaySettings().setResolutionWidth(monitor.getVideoMode().getWidth());
                        settings.getDisplaySettings().setResolutionHeight(monitor.getVideoMode().getHeight());
                        settings.getDisplaySettings().setFrameRate(monitor.getVideoMode().getRefreshRate());
                    }
                    butRes.text().setValue(String.format("%dx%d, %dHz", settings.getDisplaySettings().getResolutionWidth(), settings.getDisplaySettings().getResolutionHeight(), settings.getDisplaySettings().getFrameRate()));
                }
                else{
                    butRes.disabled().set(true);
                    settings.getDisplaySettings().setResolutionWidth(settings.getDisplaySettings().getDisplayMode() == DisplayMode.WINDOWED ? -1 : monitor.getVideoMode().getWidth());
                    settings.getDisplaySettings().setResolutionHeight(settings.getDisplaySettings().getDisplayMode() == DisplayMode.WINDOWED ? -1 : monitor.getVideoMode().getHeight());
                    settings.getDisplaySettings().setFrameRate(60);
                    butRes.text().setValue(String.format("%dx%d, %dHz", Platform.getEngineClient().getRenderManager().getWindow().getWidth(), Platform.getEngineClient().getRenderManager().getWindow().getHeight(), 60));
                }
            }
        });
        var hb1 = new HBox();
        hb1.spacing().set(10f);
        hb1.getChildren().addAll(lblDisplayMode, butDisplayMode);
        var hb2 = new HBox();
        hb2.spacing().set(10f);
        hb2.getChildren().addAll(lblRes, butRes);
        var vb = new VBox();
        vb.getChildren().addAll(hb1,hb2);

        //All
        setTopAnchor(title, 10f);
        setLeftAnchor(title, (this.width().get() - title.width().get()) / 2);
        setTopAnchor(vb, title.height().get() + 20f);
        setLeftAnchor(vb, (this.width().get() - vb.width().get()) / 2);
        var butBack = new Button("Return without Saving");
        var butSave = new Button("Save and Return");
        butBack.getSize().minHeight().set(25);
        butSave.getSize().minHeight().set(25);
        butBack.setOnMouseClicked(event -> {
            settings.getDisplaySettings().setDisplayMode(baksettings.getDisplaySettings().getDisplayMode());
            settings.getDisplaySettings().setResolutionHeight(baksettings.getDisplaySettings().getResolutionHeight());
            settings.getDisplaySettings().setResolutionWidth(baksettings.getDisplaySettings().getResolutionWidth());
            requireClose();
        });
        butSave.setOnMouseClicked(event -> {
            settings.apply();
            settings.save();
            requireClose();
        });
        butSave.border().setValue(new Border(Color.WHITE, 2));
        butBack.border().setValue(new Border(Color.WHITE, 2));
        var hb3 = new HBox();
        hb3.getChildren().addAll(butSave, butBack);
        setBottomAnchor(hb3, 10f);
        setBottomAnchor(vb, hb3.height().get() + 20f);
        setRightAnchor(hb3, 10f);
        this.getChildren().addAll(title, vb, hb3);
        ValueChangeListener<Float> sizeChangeListener = (observable, oldValue, newValue) -> {
            setTopAnchor(title, 10f);
            setLeftAnchor(title, (this.width().get() - title.width().get()) / 2);
            setTopAnchor(vb, title.height().get() + 20f);
            setLeftAnchor(vb, (this.width().get() - vb.width().get()) / 2);
            setBottomAnchor(hb3, 10f);
            setBottomAnchor(vb, hb3.height().get() + 20f);
            setRightAnchor(hb3, 10f);
            layoutChildren();
        };
        width().addChangeListener(sizeChangeListener);
        height().addChangeListener(sizeChangeListener);
    }

    @Override
    public void doClosing(GuiManager manager) {
        manager.showLastScreen();
    }
}
