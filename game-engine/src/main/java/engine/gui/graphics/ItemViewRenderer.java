package engine.gui.graphics;

import engine.graphics.item.ItemRenderManager;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.graphics.voxel.VoxelGraphics;
import engine.gui.control.ItemView;
import engine.item.BlockItem;
import org.joml.Matrix4f;

public class ItemViewRenderer implements NodeRenderer<ItemView> {

    public static final ItemViewRenderer INSTANCE = new ItemViewRenderer();

    @Override
    public void render(ItemView node, Graphics graphics) {
        node.itemStack().ifPresent(itemStack -> {
            VertexDataBuf buf = VertexDataBuf.currentThreadBuffer();
            buf.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
            ItemRenderManager.instance().generateMesh(buf, itemStack, 0);
            buf.finish();
            float size = node.size().get();
            Matrix4f modelMatrix = new Matrix4f();
            if (itemStack.getItem() instanceof BlockItem) {
                modelMatrix.translate(size * 0.5f, size * 0.5f, 0)
                        .rotate((float) -(Math.PI / 4), 0, 1, 0)
                        .rotate((float) -Math.PI / 6f, 1, 0, -1)
                        .scale(size * 0.55f, -size * 0.55f, size * 0.55f);
            } else {
                modelMatrix.rotate((float) Math.PI, 0, 1, 0)
                        .translate(-size * 0.5f, size * 0.5f, 0)
                        .scale(size, -size, 1);
            }
            graphics.drawStreamedMesh(DrawMode.TRIANGLES, buf, VoxelGraphics.getVoxelTextureAtlas().getTexture(), modelMatrix);
        });
    }
}
