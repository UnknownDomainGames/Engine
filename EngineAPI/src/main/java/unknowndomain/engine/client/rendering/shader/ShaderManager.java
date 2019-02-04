package unknowndomain.engine.client.rendering.shader;

import org.joml.*;
import unknowndomain.engine.Platform;

import java.util.HashMap;
import java.util.Optional;

public class ShaderManager {

    public static final ShaderManager INSTANCE = new ShaderManager();

    private HashMap<String, ShaderProgram> createdShaderMap;

    private ShaderProgram lastShader;

    private ShaderProgram usingShader;

    private boolean overriding;

    private ShaderManager(){
        createdShaderMap = new HashMap<>();
        overriding = false;
    }

    public ShaderProgram createShader(String name, Shader... shaders){
        if(createdShaderMap.containsKey(name)) {
            Platform.getLogger().warn(String.format("repeating creating shader program with the same name! name: %s", name));
            return createdShaderMap.get(name);
        }
        ShaderProgram sp = new ShaderProgram();
        sp.init(shaders);
        createdShaderMap.put(name, sp);
        return sp;
    }

    public void bindShader(ShaderProgram sp){
        if(!overriding){
            bindShaderInternal(sp);
        }
    }

    public void bindShader(String name){
        if(createdShaderMap.containsKey(name)){
            bindShader(createdShaderMap.get(name));
        }else{
            Platform.getLogger().warn("Shader Program %s cannot be found at Shader Manager!", name);
        }
    }

    public Optional<ShaderProgram> getShader(String name){
        return Optional.ofNullable(createdShaderMap.get(name));
    }

    private void bindShaderInternal(ShaderProgram sp){
        if(usingShader != null){
            lastShader = usingShader;
        }
        usingShader = sp;
        usingShader.use();
    }

    public void restoreShader(){
        if(!overriding && lastShader != null){
            var tmp = lastShader;
            bindShaderInternal(tmp);
        }
    }

    public void bindShaderOverriding(ShaderProgram sp){
        overriding = true;
        bindShaderInternal(sp);
    }

    public void unbindOverriding(){
        overriding = false;
        restoreShader();
    }

    public void setUniform(String location, int value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, float value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, boolean value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector2fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector3fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector4fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Matrix3fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Matrix4fc value) {
        usingShader.setUniform(location, value);
    }

    public ShaderProgram getUsingShader() {
        return usingShader;
    }
}
