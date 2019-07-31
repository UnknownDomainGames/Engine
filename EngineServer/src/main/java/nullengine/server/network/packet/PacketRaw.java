package nullengine.server.network.packet;

import com.google.gson.Gson;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class PacketRaw extends BasePacket {

    private Map<String, Object> content;

    @Override
    public void write(PacketBuf buf) throws IOException {
        var str = new Gson().toJson(content);
        var len = str.length();
        buf.writeVarInt(len);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var len = buf.readVarInt();
        var str = (String) buf.readCharSequence(len, StandardCharsets.UTF_8);
        content = new Gson().fromJson(str, Map.class);
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
}
