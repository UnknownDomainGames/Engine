package engine.server.network;

import engine.util.Side;

public class ClientGameplayNetworkHandlerContext implements NetworkHandlerContext {

    private NetworkClient client;

    public ClientGameplayNetworkHandlerContext() {
    }

    public ClientGameplayNetworkHandlerContext(ClientLoginNetworkHandlerContext context) {
        this.client = context.getClient();
    }

    @Override
    public Side getContextSide() {
        return Side.CLIENT;
    }

    @Override
    public boolean isSideDepends() {
        return true;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return ConnectionStatus.GAMEPLAY;
    }

    public NetworkClient getClient() {
        return client;
    }

    public void setClient(NetworkClient client) {
        this.client = client;
    }
}
