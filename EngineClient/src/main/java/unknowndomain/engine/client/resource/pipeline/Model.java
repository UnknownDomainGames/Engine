package unknowndomain.engine.client.resource.pipeline;

import java.util.Arrays;
import java.util.Map;

class Model {
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
        Element.Rotation rotation;
        Element.Faces faces;

        static class Faces {
            Element.Face up;
            Element.Face down;
            Element.Face north;
            Element.Face south;
            Element.Face east;
            Element.Face west;
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
