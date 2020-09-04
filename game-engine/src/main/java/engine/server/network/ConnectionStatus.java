package engine.server.network;

public enum ConnectionStatus {
    HANDSHAKE,
    LOGIN,
    GAMEPLAY_PREPARE,
    GAMEPLAY,
    SERVER_STATUS
}
