package nullengine.server.network.packet;

import nullengine.Platform;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketHandshake extends BasePacket {
    private String engineVersion;
    private List<String> mods;

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeVarInt(Platform.getVersion().length());
        buf.writeCharSequence(Platform.getVersion(), Charset.forName("ascii"));
        var modlist = Platform.getEngine().getModManager().getLoadedMods().stream().map(container -> container.getId() + ":" + container.getVersion().toString()).collect(Collectors.toList());
        buf.writeVarInt(modlist.size());
        var charset = Charset.forName("utf-8");
        for (String s : modlist) {
            buf.writeVarInt(s.length());
            buf.writeCharSequence(s, charset);
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var len = buf.readVarInt();
        engineVersion = (String) buf.readCharSequence(len, Charset.forName("ascii"));
        mods = new ArrayList<>();
        var size = buf.readVarInt();
        var charset = Charset.forName("utf-8");
        for (int i = 0; i < size; i++) {
            len = buf.readVarInt();
            mods.add((String) buf.readCharSequence(len, charset));
        }
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public List<String> getMods() {
        return mods;
    }
}
