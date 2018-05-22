package com.github.unknownstudio.unknowndomain.engine.client.util;

import java.io.*;

public class GLHelper {

    public static String readText(String path){
        StringBuilder sb = new StringBuilder();
        try(InputStream a = GLHelper.class.getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(a))){
            String str;
            while ((str = reader.readLine()) != null){
                sb.append(str).append('\n');
            }
        }
        catch (IOException e){

        }
        return sb.toString();
    }
}
