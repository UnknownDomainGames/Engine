package nullengine.enginemod.client.gui.game;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import nullengine.Platform;
import nullengine.client.gui.control.Button;
import nullengine.client.gui.control.Label;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.rendering.RenderManager;
import nullengine.server.network.ConnectionStatus;
import nullengine.server.network.NetworkClient;
import nullengine.server.network.packet.PacketHandshake;
import nullengine.util.Color;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GuiConnectServer extends BorderPane/* implements GuiTickable*/ {

    private Label lblStatus;
    private Label lblReason;

    private Button button;

    private NetworkClient networkClient;

    private boolean isCancelled = false;
    private MutableBooleanValue isFailed = new SimpleMutableBooleanValue(false);

    public GuiConnectServer(String ip, int port){
        var vbox = new VBox();
        vbox.alignment().setValue(Pos.HPos.CENTER);
        vbox.padding().setValue(new Insets(200,0,0,0));
        lblStatus = new Label();
        lblStatus.text().setValue("Connecting");
        lblReason = new Label();
        vbox.getChildren().add(lblStatus);
        button = new Button("disconnect");
        button.setOnMouseClicked(e -> {
            isCancelled = true;
            if (networkClient != null) {
                networkClient.close();
            }
            var guiManager = Platform.getEngineClient().getRenderManager().getGuiManager();
            guiManager.showLastScreen();
        });
        lblStatus.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        lblReason.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        button.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        vbox.getChildren().add(button);
        isFailed.addChangeListener((observable, oldValue, newValue) -> {
            button.text().setValue("Back");
            vbox.getChildren().remove(button);
            vbox.getChildren().add(lblReason);
            vbox.getChildren().add(button);
        });
        background().setValue(Background.fromColor(Color.fromRGB(0x7f7f7f)));
        setAlignment(vbox, Pos.CENTER);
        center().setValue(vbox);
//        addEventHandler(PacketReceivedEvent.class, event -> {
//            if(event.getPacket() instanceof PacketDisconnect){
//                Platform.getLogger().warn("Disconnected from server");
//                lblStatus.text().setValue("Disconnected");
//                lblReason.text().setValue(((PacketDisconnect) event.getPacket()).getReason());
//                isFailed.set(true);
//            }
//        });
//        addEventHandler(NetworkDisconnectedEvent.class, event ->{
//            Platform.getLogger().warn("Disconnected from server: {}", event.getReason());
//            lblStatus.text().setValue("Disconnected");
//            lblReason.text().setValue(event.getReason());
//            isFailed.set(true);
//        });
        connect(ip, port);
    }

    private void connect(final String ip, final int port){
        Platform.getLogger().info("Start connecting server at {}:{}", ip, port);
        Thread connector = new Thread(()->{
            if(isCancelled) return;
            try {
                var address = InetAddress.getByName(ip);
                networkClient = new NetworkClient();
                networkClient.run(address, port);
                networkClient.send(new PacketHandshake(ConnectionStatus.LOGIN));
            } catch (UnknownHostException ex) {
                if(isCancelled) return;
                Platform.getLogger().error("Cannot connect to server", ex);
                lblStatus.text().setValue("Disconnected");
                lblReason.text().setValue("Unknown host");
                isFailed.set(true);
            } catch (Exception ex) {
                if(isCancelled) return;
                Platform.getLogger().error("Cannot connect to server", ex);
                lblStatus.text().setValue("Disconnected");
                lblReason.text().setValue(ex.getMessage());
                isFailed.set(true);
            }
        }, "Connector");
        connector.setUncaughtExceptionHandler((thread, e) -> {

        });
        connector.start();
    }

    public void update(RenderManager context) {
//        if(networkClient != null && networkClient.getHandler() != null) {
//            if(!networkClient.getHandler().isChannelOpen()){
//                lblStatus.text().setValue("Disconnected");
//                lblReason.text().setValue("event.getReason()");
//                isFailed.set(true);
//            }
//        }
    }
}
