package nullengine.client.gui.rendering;

import com.google.common.base.Strings;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.WrapText;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.font.TextMesh;

import java.util.stream.Collectors;

public final class WrapTextRenderer implements ComponentRenderer<WrapText> {

    private WrapText wrapText;

    private boolean dirty = true;

    private float lineHeight;
    private LineMesh[] meshes;

    public WrapTextRenderer(WrapText wrapText) {
        this.wrapText = wrapText;
        wrapText.text().addChangeListener((observable, oldValue, newValue) -> dirty = true);
        wrapText.font().addChangeListener((observable, oldValue, newValue) -> dirty = true);
        wrapText.textWidth().addChangeListener((observable, oldValue, newValue) -> dirty = true);
    }

    private void bakeTextMesh() {
        dirty = false;
        String text = wrapText.text().get();
        if (Strings.isNullOrEmpty(text)) {
            meshes = null;
            return;
        }
        Font font = wrapText.font().get();
        float textWidth = wrapText.textWidth().get();
        FontHelper fontHelper = FontHelper.instance();

        var lines = text.lines().flatMap(str -> fontHelper.wrapText(str, textWidth, font).stream()).collect(Collectors.toList());
        lineHeight = fontHelper.computeTextHeight(text, font, textWidth) / lines.size();
        meshes = new LineMesh[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            meshes[i] = new LineMesh(fontHelper.bakeTextMesh(line, font), fontHelper.computeTextWidth(line, font));
        }
    }

    @Override
    public void render(WrapText text, Graphics graphics) {
        if (dirty) bakeTextMesh();
        if (meshes == null) return;

        graphics.setColor(text.color().get());
        Pos alignment = text.textAlignment().get();
        float leading = text.leading().getFloat();
        float x = text.x().get(), y = text.y().get();
        for (LineMesh mesh : meshes) {
            switch (alignment.getHpos()) {
                case RIGHT:
                    x = text.width().get() - mesh.width;
                    break;
                case CENTER:
                    x = (text.width().get() - mesh.width) / 2;
                    break;
                case LEFT:
                    break;
            }
            var y1 = y + (lineHeight * leading - lineHeight) / 2;
            graphics.drawText(mesh.mesh, x, y1);
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
