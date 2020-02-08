package engine.server.network.packet;

import java.io.IOException;

public class UnrecognizedPacketException extends IOException {

    public UnrecognizedPacketException(){
        super();
    }

    public UnrecognizedPacketException(String msg){
        super(msg);
    }
    public UnrecognizedPacketException(Throwable inner){
        super(inner);
    }
    public UnrecognizedPacketException(String msg, Throwable inner){
        super(msg, inner);
    }
}
