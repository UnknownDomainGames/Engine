package engine.graphics.gl.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Pattern;

public class ShaderInfoCollector {
    public void readShader(Path path){
        var pattern = Pattern.compile(".*\\.(vert|frag|tesc|tese|geom|comp|glsl)$");
        var matcher = pattern.matcher(path.toString());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Cannot recognize the shader type");
        }
        switch (matcher.group(1)){
            case "vert":
                break;
            case "frag":
                break;
            case "tesc":
                break;
            case "tese":
                break;
            case "geom":
                break;
            case "comp":
                break;
            case "glsl":
                break;
        }
        int version = -1;
        String profile = "core";
        try (var fileChannel = new BufferedReader(new FileReader(path.toFile()))){
            String line = null;
            var str = "";
            var contentStarted = false;
            while ((str = line != null ? line + fileChannel.readLine() : fileChannel.readLine()) != null){
                if(str.startsWith("#")){ //directives
                    if(version == -1 && !str.startsWith("#version")){
                        throw new ShaderFormatException("Shader not start with #version");
                    }
                    if(str.startsWith("#version")){
                        var ver = str.replaceFirst("#version", "").trim().split(" +");
                        if(ver.length > 2){
                            throw new ShaderFormatException("Too many things in #version directive");
                        }
                        if(ver.length < 1){
                            throw new ShaderFormatException("#version with no information");
                        }
                        if(!ver[1].contentEquals("core") && !ver[1].contentEquals("compatibility")){
                            throw new ShaderFormatException("Unknown profile");
                        }
                        try{
                            version = Integer.parseInt(ver[0]);
                        } catch (NumberFormatException e){
                            throw new ShaderFormatException("Cannot parse GLSL version", e);
                        }
                    } else if (str.startsWith("#extension")) {
                        //TODO: gather extension infos
                    } else if (str.startsWith("#include")) {
                    }
                } else if (!str.endsWith(";")) {
                    line = str;
                } else{
                    var token = str.split(" +");
                    line = null;
                }
            }
        } catch (IOException e) {

        }
    }

    private void findQualifier(String token){
        switch (token){
            case "uniform":
                break;
            case "const":
                break;
            case "in":
                break;
            case "out":
                break;
        }
    }

    private void findType(String token){
        var arrayPattern = Pattern.compile("\\w+?(\\[\\w*])+");
        if(arrayPattern.asMatchPredicate().test(token)){
            // It is an array
            var arrayContentPattern = Pattern.compile("\\[(?<constant>\\w+)|(?<size>\\d+)]");
            var matcher = arrayContentPattern.matcher(token);
            while(matcher.find()){

            }
            token = matcher.reset().replaceAll("");
        }
        switch (token){
            case "int":
                break;
            case "bool":
                break;
            case "uint":
                break;
            case "float":
                break;
            case "double":
                break;
            default:
                var looseVectorPattern = Pattern.compile("(\\w)?vec(\\d)*");
                var vectorPattern = Pattern.compile("([biud])?vec([234])");
                if(looseVectorPattern.asMatchPredicate().and(vectorPattern.asMatchPredicate().negate()).test(token)){
                    // It aims to be a vector type but the detail of this type of vector is unrecognized
                    throw new ShaderFormatException(String.format("Unrecognized vector: %s", token));
                }
                var vecMatcher = vectorPattern.matcher(token);
                if(vecMatcher.matches()){

                }
                var looseMatrixPattern = Pattern.compile("(\\w)?mat(\\w)*");
                var matrixPattern = Pattern.compile("(?<type>d)?mat(?:(?<column>[234])x(?<row>[234])|(?<size>[234]))");
                if(looseMatrixPattern.asMatchPredicate().and(matrixPattern.asMatchPredicate().negate()).test(token)){
                    // It aims to be a matrix type but the detail of this type of matrix is unrecognized
                    throw new ShaderFormatException(String.format("Unrecognized matrix: %s", token));
                }
                var matMatcher = matrixPattern.matcher(token);
                if(matMatcher.matches()){

                }

        }
    }
}
