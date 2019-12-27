package nullengine.client.rendering.font;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.font.FontPlaneTexture;
import nullengine.util.Color;

import java.util.List;

public class BakedTextMesh {

    private List<float[]> vertices;
    private CharSequence text;
    private Font font;
    private Color color;
    private FontPlaneTexture texture;

    BakedTextMesh(List<float[]> vertices, CharSequence text, Font font, Color color, FontPlaneTexture texture){
        this.vertices = vertices;
        this.text = text;
        this.font = font;
        this.color = color;
        this.texture = texture;
    }

    public CharSequence getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    public FontPlaneTexture getTexture() {
        return texture;
    }

    public void putVertices(GLBuffer buffer){
        for (float[] vertex : vertices) {
            buffer.pos(vertex, 0).color(color).uv(vertex, 3).endVertex();
        }
    }
}
