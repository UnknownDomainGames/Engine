package nullengine.enginemod.client.gui.game;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import nullengine.Platform;
import nullengine.client.gui.component.Button;
import nullengine.client.gui.component.Label;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.server.network.ConnectionStatus;
import nullengine.server.network.NetworkClient;
import nullengine.server.network.packet.PacketHandshake;
import nullengine.util.Color;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class GuiConnectServer extends BorderPane {

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
        button.setOnClick(e->{
            isCancelled = true;
            if(networkClient != null){
                networkClient.close();
            }
            super.requireClose();
        });
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
                lblReason.text().setValue("Unknown host");
                isFailed.set(true);
            } catch (Exception ex) {
                if(isCancelled) return;
                Platform.getLogger().error("Cannot connect to server", ex);
                lblReason.text().setValue(ex.getMessage());
                isFailed.set(true);
            }
        }, "Connector");
        connector.setUncaughtExceptionHandler((thread, e) -> {

        });
        connector.start();
    }

    @Override
    public void requireClose() {
        // require close by Esc may cause unintended result
    }
}
