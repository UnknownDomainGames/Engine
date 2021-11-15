package engine.graphics.voxel.shape;

import engine.graphics.Geometry;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;

public class SelectedBlock extends Geometry {

    public SelectedBlock() {
        float minX = -0.001f, maxX = 1.001f,
                minY = -0.001f, maxY = 1.001f,
                minZ = -0.001f, maxZ = 1.001f;
        VertexDataBuffer buffer = VertexDataBuffer.currentThreadBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();

        buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

        buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

        buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buffer.finish();

        setMesh(SingleBufMesh.builder().drawMode(DrawMode.LINES).build(buffer));
    }
}
