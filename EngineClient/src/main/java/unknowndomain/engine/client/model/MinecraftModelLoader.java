package unknowndomain.engine.client.model;

import com.google.gson.Gson;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.texture.TextureManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MinecraftModelLoader {
    private ResourceManager manager;
    private TextureManager textureManager;

    public MinecraftModelLoader(ResourceManager manager, TextureManager textureManager) {
        this.manager = manager;
        this.textureManager = textureManager;
    }

    public Model resolve(DomainedPath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) return null;

        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = resolve(new DomainedPath("", "minecraft/models/" + model.parent + ".json"));
            if (parent == null) throw new IllegalArgumentException("Missing parent");
            if (model.elements == null) model.elements = parent.elements;
            if (model.ambientocclusion == null) model.ambientocclusion = parent.ambientocclusion;
            if (model.display == null) model.display = parent.display;

            if (parent.textures != null) model.textures.putAll(parent.textures);
        }
        model.ambientocclusion = model.ambientocclusion == null ? false : model.ambientocclusion;

        return model;
    }

    public GLMesh bakeModel(Model model) {
        Map<String, int[]> textureOffsets = new HashMap<>();
        for (String variant : model.textures.keySet()) {
            String path = model.textures.get(variant);
            while (path.startsWith("#")) {
                String next = model.textures.get(path.substring(1, path.length()));
                if (next == null) {
                    path = null;
                    break;
                }
                path = next;
            }
            if (path == null) continue;
            int[] offset = textureManager.requireTexture(new DomainedPath("", ""));
            textureOffsets.put(variant, offset);
        }
        float[] vertices = new float[model.elements.length * 24 * 3];
        float[] uv = new float[model.elements.length * 24 * 2];
        float[] normals = new float[model.elements.length * 24 * 3];
        int[] indices = new int[model.elements.length * 24];
        int vertIndex = 0;
        int uvIndex = 0;
        final int X = 0, Y = 1, Z = 2;
        for (int i = 0; i < indices.length; i++) indices[i] = i;

        for (Model.Element element : model.elements) {
            int[] off;
            float[] thisUv;

            // front
            off = textureOffsets.get(element.faces.north.texture);
            thisUv = element.faces.north.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];


            // left
            off = textureOffsets.get(element.faces.west.texture);
            thisUv = element.faces.west.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];

            // bottom
            off = textureOffsets.get(element.faces.down.texture);
            thisUv = element.faces.down.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];

            // back
            off = textureOffsets.get(element.faces.north.texture);
            thisUv = element.faces.north.uv;
            System.arraycopy(element.to, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];


            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];

            // right
            off = textureOffsets.get(element.faces.east.texture);
            thisUv = element.faces.east.uv;
            System.arraycopy(element.to, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];

            // top
            off = textureOffsets.get(element.faces.up.texture);
            thisUv = element.faces.up.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[1] + off[1];


            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[4] + off[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3] + off[0];
            uv[uvIndex++] = thisUv[2] + off[1];

        }
        return GLMesh.from(vertices, uv, normals, indices, GL11.GL_QUADS);
    }

    static class Model {
        String parent;
        Boolean ambientocclusion;
        Display display;
        Map<String, String> textures;
        Element[] elements;

        @Override
        public String toString() {
            return "Model{" +
                    "parent='" + parent + '\'' +
                    ", ambientocclusion=" + ambientocclusion +
                    ", display=" + display +
                    ", textures=" + textures +
                    ", elements=" + Arrays.toString(elements) +
                    '}';
        }

        static class Transform {
            float[] rotation;
            float[] translation;
            float[] scale;
        }

        static class Element {
            int[] from;
            int[] to;
            boolean shade;
            Rotation rotation;
            Faces faces;

            static class Faces {
                Face up;
                Face down;
                Face north;
                Face south;
                Face east;
                Face west;
            }

            static class Face {
                float[] uv;
                String texture;
                boolean cullface;
                int rotation;
                int tintindex;
            }

            static class Rotation {
                int[] origin;
                String axis;
                int angle;
                boolean rescale;
            }
        }

        static class Display {
            Transform thirdperson_righthand;
            Transform thirdperson_lefthand;
            Transform firstperson_righthand;
            Transform firstperson_lefthand;
            Transform gui;
            Transform head;
            Transform ground;
            Transform fixed;
        }
    }
}
