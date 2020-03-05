package engine.gui.rendering;

import com.google.common.base.Strings;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.graphics.font.TextMesh;
import engine.gui.misc.Pos;
import engine.gui.text.Text;

import java.util.stream.Collectors;

public final class TextRenderer implements ComponentRenderer<Text> {

    private Text text;

    private boolean dirty = true;

    private float lineHeight;
    private LineMesh[] meshes;

    public TextRenderer(Text text) {
        this.text = text;
        text.text().addChangeListener((observable, oldValue, newValue) -> dirty = true);
        text.font().addChangeListener((observable, oldValue, newValue) -> dirty = true);
    }

    private void bakeTextMesh() {
        dirty = false;
        String text = this.text.text().get();
        if (Strings.isNullOrEmpty(text)) {
            meshes = null;
            return;
        }
        Font font = this.text.font().get();
        FontManager fontManager = FontManager.instance();

        var lines = text.lines().collect(Collectors.toList());
        lineHeight = FontManager.instance().computeTextHeight(text, font) / lines.size();
        meshes = new LineMesh[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            meshes[i] = new LineMesh(fontManager.bakeTextMesh(line, font), fontManager.computeTextWidth(line, font));
        }
    }

    @Override
    public void render(Text text, Graphics graphics) {
        if (dirty) bakeTextMesh();
        if (meshes == null) return;

        graphics.setColor(text.color().get());
        Pos alignment = text.textAlignment().get();
        float leading = text.leading().getFloat();
        float y = 0;
        for (LineMesh mesh : meshes) {
            var y1 = y + (lineHeight * leading - lineHeight) / 2;
            switch (alignment.getHpos()) {
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
