package engine.server.network;

import engine.player.Profile;
import engine.util.Side;

public class ServerLoginNetworkHandlerContext implements NetworkHandlerContext {

    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public Side getContextSide() {
        return Side.SERVER;
    }

    @Override
    public boolean isSideDepends() {
        return true;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return ConnectionStatus.LOGIN;
    }
}
