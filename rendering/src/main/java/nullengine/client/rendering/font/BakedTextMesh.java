package nullengine.client.rendering.font;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.font.FontPlaneTexture;
import nullengine.util.Color;

import java.util.List;

public class BakedTextMesh {

    private List<float[]> vertices;
    private TextInfo textInfo;
    private FontPlaneTexture texture;

    BakedTextMesh(List<float[]> vertices, CharSequence text, Font font, FontPlaneTexture texture){
        this.vertices = vertices;
        this.textInfo = new TextInfo(text, font);
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

    public FontPlaneTexture getTexture() {
        return texture;
    }

    public void putVertices(GLBuffer buffer, Color color){
        for (float[] vertex : vertices) {
            buffer.pos(vertex, 0).color(color).uv(vertex, 3).endVertex();
        }
    }
}
