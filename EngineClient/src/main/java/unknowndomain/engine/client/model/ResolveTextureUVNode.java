package unknowndomain.engine.client.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import de.matthiasmann.twl.utils.PNGDecoder;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.model.MinecraftModelLoader.Model;
import java.nio.ByteBuffer;

class ResolveTextureUVNode implements PipelineNode<Model[]> {
    private ResourceManager resourceManager;
    private Map<String, TexturePart> required;

    class TexturePart {
        int width;
        int height;
        int offsetX;
        int offsetY;
        ByteBuffer buffer;

        TexturePart(int width, int height, int offsetX, int offsetY, ByteBuffer buffer) {
            this.width = width;
            this.height = height;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.buffer = buffer;
        }
    }

    private void requireTexture(String path) throws IOException {
        if (required.containsKey(path))
            return;
        Resource resource = resourceManager.load(new DomainedPath("", path));
        PNGDecoder decoder = new PNGDecoder(resource.open());
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decodeFlipped(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        required.put(path, new TexturePart(decoder.getWidth(), decoder.getHeight(), 0, 0, buf));
    }

    @Override
    public void process(Model[] models, BiConsumer<String, Object> emit) throws IOException {
        for (Model model : models) {
            for (String variant : model.textures.keySet()) {
                String path = model.textures.get(variant);
                while (path.startsWith("#")) {
                    String next = model.textures.get(path.substring(1, path.length()));
                    if (next == null) {
                        path = null;
                        break;
                    }
                    path = next;
                }
                if (path == null)
                    continue;
                model.textures.put(variant, path);
                requireTexture(path);
            }
        }

        for (Model m : models) {
            for (Model.Element e : m.elements) {
                fixUV(m.textures, e.faces.up);
                fixUV(m.textures, e.faces.down);
                fixUV(m.textures, e.faces.north);
                fixUV(m.textures, e.faces.west);
                fixUV(m.textures, e.faces.east);
                fixUV(m.textures, e.faces.south);
            }
        }
        emit.accept("ModelMapped", models);
        emit.accept("TextureMap", null);
    }

    private void fixUV(Map<String, String> textures, Model.Element.Face face) {
        String path = textures.get(face.texture);
        TexturePart part = required.get(path);
        face.uv[0] += part.offsetX;
        face.uv[1] += part.offsetY;
        face.uv[2] += part.offsetX;
        face.uv[3] += part.offsetY;
    }
}