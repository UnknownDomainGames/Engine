package unknowndomain.engine.client.model.pipeline;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ResolveTextureUVNodeTest {

    @Test
    void stitch() {
        ResolveTextureUVNode node = new ResolveTextureUVNode();
        node.stitch(Lists.newArrayList());

        ArrayList<ResolveTextureUVNode.TexturePart> parts = Lists.newArrayList(
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(16, 16, null),
                new ResolveTextureUVNode.TexturePart(32, 32, null)
        );
        int dim = node.stitch(parts);
        for (ResolveTextureUVNode.TexturePart part : parts) {
            System.out.println(part);
        }
        System.out.println(dim);
    }
}