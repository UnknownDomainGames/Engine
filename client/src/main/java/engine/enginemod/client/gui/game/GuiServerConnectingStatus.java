package engine.enginemod.client.gui.game;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import engine.Platform;
import engine.client.game.GameClientMultiplayer;
import engine.game.MultiplayerGameData;
import engine.graphics.GraphicsManager;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.HPos;
import engine.gui.misc.Pos;
import engine.gui.text.Text;
import engine.gui.text.WrapText;
import engine.server.event.NetworkDisconnectedEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.NetworkClient;
import engine.server.network.packet.PacketDisconnect;
import engine.server.network.packet.PacketGameData;
import engine.server.network.packet.PacketHandshake;
import engine.util.Color;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GuiServerConnectingStatus extends FlowPane/* implements GuiTickable*/ {

    private Text lblStatus;
    private Text lblReason;

    private Button button;

    private NetworkClient networkClient;

    private boolean isCancelled = false;
    private MutableBooleanValue isFailed = new SimpleMutableBooleanValue(false);

    public GuiServerConnectingStatus(String ip, int port) {
        alignment().set(Pos.CENTER);
        var vbox = new VBox();
        getChildren().add(vbox);
        vbox.alignment().set(HPos.CENTER);
        lblStatus = new Text("Connecting");
        lblReason = new WrapText();
        vbox.getChildren().add(lblStatus);
        button = new Button("disconnect");
        button.setOnMouseClicked(e -> {
            isCancelled = true;
            if (networkClient != null) {
                networkClient.close();
            }
            var engineClient = Platform.getEngineClient();
//            engineClient.getEventBus().unregister();
            var guiManager = engineClient.getGraphicsManager().getGUIManager();
            guiManager.showLast();
        });
        lblStatus.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        lblReason.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        button.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        vbox.getChildren().add(button);
        isFailed.addChangeListener((observable, oldValue, newValue) -> {
            button.text().set("Back");
            vbox.getChildren().remove(button);
            vbox.getChildren().add(lblReason);
            vbox.getChildren().add(button);
        });
        setBackground(Background.fromColor(Color.fromRGB(0x7f7f7f)));
        Platform.getEngine().getEventBus().<PacketReceivedEvent<PacketDisconnect>, PacketDisconnect>addGenericListener(PacketDisconnect.class, event -> {
            Platform.getLogger().warn("Disconnected from server");
            lblStatus.text().set("Disconnected");
            lblReason.text().set(event.getPacket().getReason());
            isFailed.set(true);
        });
        Platform.getEngine().getEventBus().<PacketReceivedEvent<PacketGameData>, PacketGameData>addGenericListener(PacketGameData.class, event -> {
            lblStatus.text().set("Initializing game");
            var game = new GameClientMultiplayer(Platform.getEngineClient(), MultiplayerGameData.fromPacket(event.getPacket()));
            Platform.getEngine().startGame(game);
            Platform.getEngineClient().getGraphicsManager().getGUIManager().close();
        });
        Platform.getEngine().getEventBus().<NetworkDisconnectedEvent>addListener(event -> {
            Platform.getLogger().warn("Disconnected from server: {}", event.getReason());
            lblStatus.text().set("Disconnected");
            lblReason.text().set(event.getReason());
            isFailed.set(true);
        });
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
//                networkClient.send(new PacketHandshake(ConnectionStatus.LOGIN));
                networkClient.send(new PacketHandshake(ConnectionStatus.GAMEPLAY)); //TODO: we should go to login status first
            } catch (UnknownHostException ex) {
                if (isCancelled) return;
                Platform.getLogger().error("Cannot connect to server", ex);
                lblStatus.setText("Disconnected");
                lblReason.setText("Unknown host");
                isFailed.set(true);
            } catch (Exception ex) {
                if (isCancelled) return;
                Platform.getLogger().error("Cannot connect to server", ex);
                lblStatus.setText("Disconnected");
                lblReason.setText(ex.getMessage());
                isFailed.set(true);
            }
        }, "Connector");
        connector.setUncaughtExceptionHandler((thread, e) -> {
            Platform.getLogger().error("Cannot connect to server", e);
            lblStatus.text().set("Disconnected");
            lblReason.text().set(String.format("Connector met unexpected exception:%s", e.getMessage()));
            isFailed.set(true);
        });
        connector.start();
    }

    public void update(GraphicsManager context) {
//        if(networkClient != null && networkClient.getHandler() != null) {
//            if(!networkClient.getHandler().isChannelOpen()){
//                lblStatus.text().set("Disconnected");
//                lblReason.text().set("event.getReason()");
//                isFailed.set(true);
//            }
//        }
    }
}
