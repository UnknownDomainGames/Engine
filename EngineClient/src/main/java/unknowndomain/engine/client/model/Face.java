package unknowndomain.engine.client.model;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public List<? extends VertexR> vertices;

    public Face(List<? extends VertexR> vertices){
        this.vertices = new ArrayList<>(vertices);
    }

    public static class ObjFace{
        private IndexGroup[] indexGroups = new IndexGroup[3];

        public ObjFace(String v1, String v2, String v3) {
            indexGroups = new IndexGroup[3];
            // Parse the lines
            indexGroups[0] = parseLine(v1);
            indexGroups[1] = parseLine(v2);
            indexGroups[2] = parseLine(v3);
        }

        private IndexGroup parseLine(String line) {
            IndexGroup idxGroup = new IndexGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]);
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTex = textCoord.length() > 0 ? Integer.parseInt(textCoord)  : 0;
                if (length > 2) {
                    idxGroup.idxNormal = Integer.parseInt(lineTokens[2]);
                }
            }

            return idxGroup;
        }

        public IndexGroup[] getIndexGroups() {
            return indexGroups;
        }

        public static class IndexGroup{
            public int idxPos;
            public int idxTex;
            public int idxNormal;

            public IndexGroup(){
                idxNormal = 0;
                idxPos = 0;
                idxTex = 0;
            }
        }
    }
}
