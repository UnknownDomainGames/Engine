package engine.server.network;

import engine.util.Side;

public class HandshakeNetworkHandlerContext implements NetworkHandlerContext {

    private NetworkEndpoint endpoint;

    @Override
    public Side getContextSide() {
        return null;
    }

    @Override
    public boolean isSideDepends() {
        return false;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return ConnectionStatus.HANDSHAKE;
    }

    public NetworkEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(NetworkEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
