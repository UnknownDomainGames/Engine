package nullengine.server.network.packet;

import nullengine.Engine;
import nullengine.Platform;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketHandshake extends BasePacket {
    private String engineVersion;
    private List<String> mods;

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeVarInt(Platform.getVersion().length());
        buf.writeCharSequence(Platform.getVersion(), StandardCharsets.US_ASCII);
        var modlist = Platform.getEngine().getModManager().getLoadedMods().stream().map(container -> container.getId() + ":" + container.getVersion().toString()).collect(Collectors.toList());
        buf.writeVarInt(modlist.size());
        var charset = StandardCharsets.UTF_8;
        for (String s : modlist) {
            buf.writeVarInt(s.length());
            buf.writeCharSequence(s, charset);
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var len = buf.readVarInt();
        engineVersion = buf.readCharSequence(len, StandardCharsets.US_ASCII).toString();
        mods = new ArrayList<>();
        var size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            len = buf.readVarInt();
            mods.add(buf.readCharSequence(len, StandardCharsets.UTF_8).toString());
        }
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public List<String> getMods() {
        return mods;
    }
}
