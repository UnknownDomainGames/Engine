package nullengine.client.rendering.font;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.font.FontPlaneTexture;
import nullengine.util.Color;

import java.util.List;

public class BakedTextMesh {

    private List<float[]> vertices;
    private TextInfo textInfo;
    private FontPlaneTexture texture;

    BakedTextMesh(List<float[]> vertices, CharSequence text, Font font, Color color, FontPlaneTexture texture){
        this.vertices = vertices;
        this.textInfo = new TextInfo(text, font, color);
        this.texture = texture;
    }

    public TextInfo getTextInfo(){
        return textInfo;
    }

    public CharSequence getText() {
        return getTextInfo().getText();
    }

    public Font getFont() {
        return getTextInfo().getFont();
    }

    public Color getColor() {
        return getTextInfo().getColor();
    }

    public FontPlaneTexture getTexture() {
        return texture;
    }

    public void putVertices(GLBuffer buffer){
        for (float[] vertex : vertices) {
            buffer.pos(vertex, 0).color(getColor()).uv(vertex, 3).endVertex();
        }
    }
}
