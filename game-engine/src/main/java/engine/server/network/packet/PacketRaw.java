package engine.server.network.packet;

import com.google.gson.Gson;
import engine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class PacketRaw implements Packet {

    private Map<String, Object> content;

    @Override
    public void write(PacketBuf buf) throws IOException {
        var str = new Gson().toJson(content);
        var len = str.length();
        buf.writeVarInt(len);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(PacketBuf buf) throws IOException {
        var len = buf.readVarInt();
        var str = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        content = new Gson().fromJson(str, Map.class); // @TODO: use singleton Gson instance
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
}
