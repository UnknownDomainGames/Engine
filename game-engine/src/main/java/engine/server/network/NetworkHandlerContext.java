package engine.server.network;

import engine.util.Side;

public interface NetworkHandlerContext {
    /**
     * Get which side this context is
     *
     * @return <code>Side.CLIENT</code> or <code>Side.SERVER</code>
     */
    Side getContextSide();

    /**
     * Check if what <code>getContextSide</code> returns really matters. This is useful when the context is side-independent
     *
     * @return true if context side matters, otherwise false
     */
    boolean isSideDepends();

    /**
     * Get which connection status this context type for
     *
     * @return suitable Connection Status
     */
    ConnectionStatus getConnectionStatus();
}
