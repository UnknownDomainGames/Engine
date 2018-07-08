package unknowndomain.engine.client.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OBJLoader {
    public static ModelObj loadObjModel(String path){

        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            List<Vector3f> vertex = new ArrayList<>();
            List<Vector3f> normal = new ArrayList<>();
            List<Vector2f> tex = new ArrayList<>();
            List<Face.ObjFace> face = new ArrayList<>();
            reader.lines().forEach(line->{
                String[] tokens = line.split("\\s+");
                switch (tokens[0]){
                    case "#": //comment line
                        break;
                    case "v": //vertex line
                        vertex.add(new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])));
                        break;
                    case "vn": //normal line
                        normal.add(new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])));
                        break;
                    case "vt": //texcoord line
                        tex.add(new Vector2f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2])));
                        break;
                    case "f":
                        face.add(new Face.ObjFace(tokens[1],tokens[2],tokens[3]));
                        break;
                }
            });
            List<Face> realface = new ArrayList<>();
            for (Face.ObjFace of :face) {
                List<Vertex> vertices = new ArrayList<>();
                for (Face.ObjFace.IndexGroup gp: of.getIndexGroups()) {
                    Vertex vertex1 = new Vertex();
                    vertex1.setUseColor(false);
                    if(gp.idxTex != 0){
                        Vector2f v = tex.get(gp.idxTex < 0 ? tex.size() + gp.idxTex : gp.idxTex - 1);
                        vertex1.setTexCoord(v.x,v.y);
                    }else{
                        vertex1.setUseTexCoord(false);
                    }
                    Vector3f v1 = vertex.get(gp.idxPos < 0 ? vertex.size() + gp.idxPos : gp.idxPos - 1);
                    vertex1.setVertex(v1.x,v1.y,v1.z);
                    if(gp.idxNormal != 0){
                    v1 = normal.get(gp.idxNormal < 0 ? normal.size() + gp.idxNormal : gp.idxNormal - 1);
                    vertex1.setNormal(v1.x,v1.y,v1.z);
                    }else{
                        vertex1.setUseNormal(false);
                    }
                    vertices.add(vertex1);
                }
                Face f = new Face(vertices);
                realface.add(f);
            }
            ModelObj mo = new ModelObj(realface);
            return mo;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
