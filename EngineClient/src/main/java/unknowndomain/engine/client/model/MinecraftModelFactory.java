package unknowndomain.engine.client.model;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.texture.GLTextureMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public final class MinecraftModelFactory {
    private int dimension = 256;
    private ResourceManager manager;

    private MinecraftModelFactory(ResourceManager manager) {
        this.manager = manager;
    }

    public static Result process(ResourceManager manager, List<ResourcePath> paths) throws IOException {
        return new MinecraftModelFactory(manager).process(paths);
    }

    Result process(List<ResourcePath> paths) throws IOException {
        GLTexture glTexture;
        List<Model> models = Lists.newArrayList();
        for (ResourcePath path : paths) {
            models.add(loadModel(path));
        }
        glTexture = resolveUV(models);
        List<Mesh> meshes = Lists.newArrayList();
        for (Model model : models) {
            meshes.add(bakeModel(model));
        }
        return new Result(glTexture, meshes);
    }

    /**
     * First step, load the model file to raw model
     *
     * @param path
     * @return
     * @throws IOException
     */
    Model loadModel(ResourcePath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) {
            Engine.getLogger().warn("Cannot load model " + path);
            return null;
        }
        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = loadModel(new ResourcePath("", "minecraft/models/" + model.parent + ".json"));
            if (parent == null) throw new IllegalArgumentException("Missing parent");
            if (model.elements == null) model.elements = parent.elements;
            if (model.ambientocclusion == null) model.ambientocclusion = parent.ambientocclusion;
            if (model.display == null) model.display = parent.display;

            if (parent.textures != null) model.textures.putAll(parent.textures);
        }
        model.ambientocclusion = model.ambientocclusion == null ? false : model.ambientocclusion;
        return model;
    }

    /**
     * second step, resolve the texture dependency, build texture and map uv
     *
     * @param models The models
     * @return The texture map
     * @throws Exception
     */
    GLTexture resolveUV(List<Model> models) throws IOException {
        Map<String, TexturePart> required = new HashMap<>();
        List<TexturePart> parts = Lists.newArrayList();
        for (Model model : models) {
            if (model == null) continue;
            for (String variant : model.textures.keySet()) {
                String path = model.textures.get(variant);
                while (path.startsWith("#")) {
                    String next = model.textures.get(path.substring(1));
                    if (next == null) {
                        Engine.getLogger().warn("Missing texture for " + variant);
                        path = null;
                        break;
                    }
                    path = next;
                }

                if (path == null) {
                    Engine.getLogger().warn("Missing texture for " + model);
                    continue;
                }
                model.textures.put(variant, path);
                if (!required.containsKey(path)) {
                    Resource resource = manager.load(new ResourcePath("", "minecraft/textures/" + path + ".png"));
                    PNGDecoder decoder = new PNGDecoder(resource.open());
                    ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
                    decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
                    buf.flip();
                    TexturePart part = new TexturePart(decoder.getWidth(), decoder.getHeight(), buf);
                    required.put(path, part);
                    parts.add(part);
                }
            }
        }

        int dimension = stitch(parts);
        GLTextureMap glTexture = new GLTextureMap(glGenTextures(), dimension);
        glBindTexture(GL_TEXTURE_2D, glTexture.id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        nglTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, dimension, dimension, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, 0);
        for (TexturePart part : parts) {
            glTexSubImage2D(GL_TEXTURE_2D, 0, part.offsetX, dimension - part.offsetY - part.height, part.width, part.height,
                    GL_RGBA, GL_UNSIGNED_BYTE, part.buffer);
        }
        glGenerateMipmap(GL_TEXTURE_2D);

        for (Model m : models) {
            if (m == null) continue;
            for (Model.Element e : m.elements) {
                Lists.newArrayList(e.faces.up, e.faces.down, e.faces.north, e.faces.west, e.faces.east, e.faces.south)
                        .forEach((face) -> {
                            if (face == null) return;
                            String path = m.textures.get(face.texture.substring(1));
                            TexturePart p = required.get(path);
                            if (face.uv == null) face.uv = new float[]{0, 0, 16, 16};
                            face.uv[0] = (face.uv[0] + p.offsetX) / dimension;
                            face.uv[1] = 1 - (face.uv[1] + p.offsetY) / dimension;
                            face.uv[2] = (face.uv[2] + p.offsetX) / dimension;
                            face.uv[3] = 1 - (face.uv[3] + p.offsetY) / dimension;
                        });
            }
        }

        return glTexture;
    }

    /**
     * stitch the textures
     *
     * @param parts
     * @return
     */
    int stitch(List<TexturePart> parts) {
        parts.sort(Comparator.<TexturePart>comparingInt(a -> a.height).reversed());
        PriorityQueue<FreeSpace> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a.height * a.width));
        List<FreeSpace> unaccepted = new ArrayList<>();
        int dimension = this.dimension;
        queue.add(new FreeSpace(0, 0, dimension, dimension));

        for (TexturePart part : parts) {
            boolean accepted = false;
            while (!accepted) {
                while (!queue.isEmpty()) {
                    FreeSpace free = queue.poll();
                    if (free == null) {
                        break;
                    }
                    if (free.accept(queue, part)) {
                        accepted = true;
                        break;
                    }
                    unaccepted.add(free);
                }
                unaccepted.addAll(queue);
                queue.clear();

                for (FreeSpace a : unaccepted) {
                    if (!a.valid) continue;
                    for (int i = 0; i < unaccepted.size(); i++) {
                        FreeSpace b = unaccepted.get(i);
                        if (a == b || !b.valid || !a.merge(b)) continue;
                        i = 0;
                    }
                }
                for (FreeSpace space : unaccepted)
                    if (space.valid)
                        queue.add(space);

                unaccepted.clear();
                if (!accepted) {
                    queue.add(new FreeSpace(dimension, 0, dimension, dimension));
                    queue.add(new FreeSpace(0, dimension, dimension + dimension, dimension));
                    dimension = dimension * 2;
                }
            }
        }
        return dimension;
    }

    Mesh bakeModel(Model model) {
        if (model == null) return null;
        float[] vertices = new float[model.elements.length * 24 * 3];
        float[] uv = new float[model.elements.length * 24 * 2];
        float[] normals = new float[model.elements.length * 24 * 3];

        List<Integer> indices = new ArrayList<>();
        int vertIndex = 0;
        int uvIndex = 0;
        int indx = 0;
        final int X = 0, Y = 1, Z = 2;

        for (Model.Element e : model.elements) {
            for (int i = 0; i < e.from.length; i++) {
                e.from[i] /= 16f;
            }
            for (int i = 0; i < e.to.length; i++) {
                e.to[i] /= 16f;
            }
        }

        for (Model.Element element : model.elements) {
            float[] thisUv;

            // north
            if (element.faces.north != null) {
                thisUv = element.faces.north.uv;
                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];


                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }

            // south
            if (element.faces.south != null) {
                thisUv = element.faces.south.uv;
                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }


            // left
            if (element.faces.west != null) {
                thisUv = element.faces.west.uv;
                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];


                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }

            if (element.faces.east != null) {
                // right
                thisUv = element.faces.east.uv;
                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }

            // bottom
            if (element.faces.down != null) {
                thisUv = element.faces.down.uv;
                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.from[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }

            // top
            if (element.faces.up != null) {
                thisUv = element.faces.up.uv;
                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[1];

                vertices[vertIndex++] = element.to[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[0];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.from[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[3];

                vertices[vertIndex++] = element.from[X];
                vertices[vertIndex++] = element.to[Y];
                vertices[vertIndex++] = element.to[Z];
                uv[uvIndex++] = thisUv[2];
                uv[uvIndex++] = thisUv[1];

                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }

        }
        int[] ind = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) ind[i] = indices.get(i);
        return new Mesh(vertices, uv, normals, ind, GL11.GL_TRIANGLES);
    }

    static class TexturePart {
        int width;
        int height;
        int offsetX;
        int offsetY;
        ByteBuffer buffer;

        TexturePart(int width, int height, ByteBuffer buffer) {
            this.width = width;
            this.height = height;
            this.buffer = buffer;
        }

        @Override
        public String toString() {
            return "TexturePart{" +
                    "width=" + width +
                    ", height=" + height +
                    ", offsetX=" + offsetX +
                    ", offsetY=" + offsetY +
                    '}';
        }
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

    public class Result {
        public final GLTexture textureMap;
        public final List<Mesh> meshes;

        private Result(GLTexture textureMap, List<Mesh> meshes) {
            this.textureMap = textureMap;
            this.meshes = meshes;
        }
    }

    class FreeSpace {
        int x, y, width, height;
        boolean valid = true;

        FreeSpace(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean merge(FreeSpace other) {
            if (x + width == other.x && other.height == this.height) {
                this.width += other.width;
                other.valid = false;
                return true;
            }
            if (x + height == other.y && other.width == this.width) {
                this.height += other.height;
                other.valid = false;
                return true;
            }
            return false;
        }

        boolean accept(PriorityQueue<FreeSpace> others, TexturePart part) {
            if (part.width <= width && part.height <= height) {
                int remainedWidth = width - part.width;
                int remainedHeight = height - part.height;
                part.offsetX = x;
                part.offsetY = y;
                if (remainedHeight != 0 && remainedWidth != 0) {
                    others.add(new FreeSpace(x + part.width, y, remainedWidth, part.height));
                    others.add(new FreeSpace(x, y + part.height, width + remainedWidth, remainedHeight));
                } else if (remainedWidth != 0)
                    others.add(new FreeSpace(x + part.width, y, remainedWidth, part.height));
                else
                    others.add(new FreeSpace(x, y + part.height, part.width, remainedHeight));
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "FreeSpace{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
