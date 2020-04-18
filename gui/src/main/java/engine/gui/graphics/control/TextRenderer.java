package engine.gui.graphics.control;

import com.google.common.base.Strings;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.graphics.font.TextMesh;
import engine.gui.control.Text;
import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.misc.Pos;

public final class TextRenderer implements NodeRenderer<Text> {

    private final Text textNode;

    private boolean dirty;
    private float lineHeight;
    private LineMesh[] meshes;

    public TextRenderer(Text text) {
        textNode = text;
    }

    public void markDirty() {
        dirty = true;
    }

    private void bakeTextMesh() {
        dirty = false;
        String text = textNode.getText();
        if (Strings.isNullOrEmpty(text)) {
            meshes = null;
            return;
        }
        Font font = textNode.getFont();
        float textWidth = textNode.getTextWidth();
        FontManager fontManager = FontManager.instance();
        meshes = text.lines()
                .flatMap(line -> fontManager.wrapText(line, textWidth, font).stream())
                .map(line -> new LineMesh(fontManager.bakeTextMesh(line, font), fontManager.computeTextWidth(line, font)))
                .toArray(LineMesh[]::new);
        lineHeight = fontManager.computeTextHeight(text, font, textWidth) / meshes.length;
    }

    @Override
    public void render(Text text, Graphics graphics) {
        if (dirty) bakeTextMesh();
        if (meshes == null) return;

        graphics.setColor(text.getColor());
        Pos alignment = text.getTextAlignment();
        float leading = text.getLeading();
        float y = 0;
        for (LineMesh mesh : meshes) {
            float y1 = y + (lineHeight * leading - lineHeight) / 2;
            switch (alignment.getHPos()) {
                case RIGHT:
                    graphics.drawText(mesh.mesh, text.getWidth() - mesh.width, y1);
                    break;
                case CENTER:
                    graphics.drawText(mesh.mesh, (text.getWidth() - mesh.width) / 2, y1);
                    break;
                case LEFT:
                    graphics.drawText(mesh.mesh, 0, y1);
                    break;
            }
            y += lineHeight * leading;
        }
    }

    private static class LineMesh {
        private TextMesh mesh;
        private float width;

        public LineMesh(TextMesh mesh, float width) {
            this.mesh = mesh;
            this.width = width;
        }
    }
}
