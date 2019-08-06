package nullengine.server.network.packet;

import com.google.gson.Gson;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacketDisconnect extends BasePacket {

    private String reason;
    private Map<String, Object> extra = new HashMap<>();

    public PacketDisconnect(){
        this("");
    }

    public PacketDisconnect(String reason){
        this.reason = reason;
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        var len = reason.length();
        buf.writeVarInt(len);
        buf.writeCharSequence(reason, StandardCharsets.UTF_8);
        if(extra.isEmpty()){
            buf.writeBoolean(false);
        }
        else {
            buf.writeBoolean(true);
            var str = new Gson().toJson(extra);
            len = str.length();
            buf.writeVarInt(len);
            buf.writeCharSequence(str, StandardCharsets.UTF_8);
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var len = buf.readVarInt();
        reason = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        if(buf.readBoolean()){
            len = buf.readVarInt();
            var str = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            extra = new Gson().fromJson(str, Map.class);
        }
        else{
            extra = new HashMap<>();
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }
}
