package engine.server.network;

import engine.util.Side;

public class ClientLoginNetworkHandlerContext implements NetworkHandlerContext {

    private NetworkClient client;

    public ClientLoginNetworkHandlerContext() {

    }

    public ClientLoginNetworkHandlerContext(HandshakeNetworkHandlerContext context) {
        if (context.getEndpoint() instanceof NetworkClient) {
            this.client = (NetworkClient) context.getEndpoint();
        } else {
            throw new IllegalArgumentException("try passing to client login context with server handshake context");
        }
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
        return ConnectionStatus.LOGIN;
    }

    public NetworkClient getClient() {
        return client;
    }

    public void setClient(NetworkClient client) {
        this.client = client;
    }
}
