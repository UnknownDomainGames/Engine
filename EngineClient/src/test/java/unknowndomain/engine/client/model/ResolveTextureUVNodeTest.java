package unknowndomain.engine.client.model;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.resource.pipeline.ResolveTextureUVNode;

import java.util.ArrayList;

public class ResolveTextureUVNodeTest {
    @Test
    public void stitch() {
        ResourceManager manager = new ResourceManager();
        manager.addResourceSource(new ResourceSourceBuiltin());
        ResolveTextureUVNode node = new ResolveTextureUVNode(manager);
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