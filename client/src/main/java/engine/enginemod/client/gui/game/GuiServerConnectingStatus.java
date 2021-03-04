package engine.enginemod.client.gui.game;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import engine.Platform;
import engine.event.Listener;
import engine.event.Order;
import engine.graphics.GraphicsManager;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Text;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.HPos;
import engine.gui.misc.Pos;
import engine.server.event.NetworkDisconnectedEvent;
import engine.server.event.NetworkingStartEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.NetworkClient;
import engine.server.network.packet.PacketHandshake;
import engine.server.network.packet.s2c.PacketGameData;
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

    private GuiServerConnectingStatus() {
        alignment().set(Pos.CENTER);
        var vbox = new VBox();
        getChildren().add(vbox);
        vbox.alignment().set(HPos.CENTER);
        lblStatus = new Text("Connecting");
        lblReason = new Text();
        lblReason.setVisible(false);
        vbox.getChildren().add(lblStatus);
        button = new Button("disconnect");
        button.setOnMouseClicked(e -> {
            isCancelled = true;
            if (networkClient != null) {
                networkClient.close();
                networkClient = null;
            }
            Platform.getEngine().getEventBus().unregister(this);
            var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
            guiManager.showLast();
            if (!guiManager.isShowing()) {
                guiManager.show(new Scene(new GUIMainMenu()));
            }
        });
        lblStatus.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        lblReason.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        button.text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        vbox.getChildren().add(lblReason);
        vbox.getChildren().add(button);
        isFailed.addChangeListener((observable, oldValue, newValue) -> {
            button.text().set("Back");
            lblReason.setVisible(true);
//            vbox.getChildren().remove(button);
//            vbox.getChildren().add(lblReason);
//            vbox.getChildren().add(button);
        });
        setBackground(Background.fromColor(Color.fromRGB(0x7f7f7f)));
    }

    public GuiServerConnectingStatus(String ip, int port) {
        this();
        Platform.getEngine().getEventBus().register(this);
        connect(ip, port);
    }

    @Listener
    public void onNetworkingStart(NetworkingStartEvent e) {
        var bus = e.getNetworkingEventBus();
        bus.<PacketReceivedEvent<PacketGameData>, PacketGameData>addGenericListener(PacketGameData.class, Order.EARLY, event -> {
            Platform.getEngine().getEventBus().unregister(this); //TODO: not supposed to be done here
            if (event.getHandler().isChannelOpen()) {
                lblStatus.text().set("Initializing game");
//                Platform.getEngineClient().getGraphicsManager().getGUIManager().close();
//                var game = new GameClientMultiplayer(Platform.getEngineClient(), networkClient, MultiplayerGameData.fromPacket(event.getPacket()));
//                Platform.getEngine().startGame(game);
            }
        });
        Platform.getEngine().getEventBus().<NetworkDisconnectedEvent>addListener(event -> {
            if (Platform.getEngine().getCurrentClientGame() != null) {
                Platform.getEngine().getCurrentClientGame().terminate();
            } else {
                networkClient.close();
            }
            if (!event.getReason().equals("")) {
                launchDisconnectedScreen(event.getReason());
            }
        });
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
//                networkClient.send(new PacketHandshake(ConnectionStatus.GAMEPLAY)); //TODO: we should go to login status first
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

    public void setStatus(boolean isFailed, String status) {
        if (isFailed) {
            this.lblStatus.text().set("Disconnected");
            this.isFailed.set(true);
            this.lblReason.text().set(status);
        }
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

    public static void launchDisconnectedScreen(String reason) {
        if (!Platform.getEngineClient().getGraphicsManager().getGUIManager().isShowing() ||
                !(Platform.getEngineClient().getGraphicsManager().getGUIManager().getShowingScene().root().get() instanceof GuiServerConnectingStatus)) { //Disconnected in game
            var root = new GuiServerConnectingStatus();
            root.lblStatus.setText("Disconnected");
            root.setStatus(true, reason);
            Platform.getEngineClient().getGraphicsManager().getGUIManager().close();
            Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(root));
        } else {
            var root = ((GuiServerConnectingStatus) Platform.getEngineClient().getGraphicsManager().getGUIManager().getShowingScene().root().get());
            root.lblStatus.text().set("Disconnected");
            root.setStatus(true, reason);
        }
    }
}
