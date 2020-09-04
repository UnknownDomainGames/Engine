package engine.server.network;

import java.net.InetAddress;

public interface NetworkEndpoint {
    void run(InetAddress address, int port);

    void tick();

    void close();
}
