package nullengine.client.rendering.model.block;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.TextureAtlasPart;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.Map;

final class ModelData {

    AssetURL url;
    Map<String, String> textures;
    Cube[] cubes;
    boolean[] fullFaces;

    static final class Cube implements Cloneable {
        Vector3fc from;
        Vector3fc to;
        Face[] faces;

        static final class Face implements Cloneable {
            String texture;
            TextureAtlasPart resolvedTexture;
            Vector4fc uv;
            byte cullFaces;

            @Override
            public Face clone() {
                try {
                    Face face = (Face) super.clone();
                    face.texture = texture;
                    face.uv = uv;
                    face.cullFaces = cullFaces;
                    return face;
                } catch (CloneNotSupportedException e) {
                    throw new InternalError(e);
                }
            }
        }

        @Override
        public Cube clone() {
            try {
                Cube cube = (Cube) super.clone();
                cube.from = from;
                cube.to = to;
                cube.faces = new Face[faces.length];
                for (int i = 0; i < faces.length; i++) {
                    cube.faces[i] = faces[i].clone();
                }
                return cube;
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
        }
    }
}
