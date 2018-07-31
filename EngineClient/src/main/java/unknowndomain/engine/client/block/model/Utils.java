package unknowndomain.engine.client.block.model;

/**
 * IO读取 如果打包成jar需修改
 * @author Administrator
 *
 */
public class Utils {
	public static String getVertex() {
		return "#version 330\n" + 
				"\n" + 
				"layout (location=0) in vec3 position;\n" + 
				"layout (location=2) in vec2 texCoord;\n" +
				"\n" + 
				"out vec2 outTexCoord;\n" + 
				"\n" + 
				"uniform mat4 modelViewMatrix;\n" + 
				"uniform mat4 projectionMatrix;\n" + 
				"\n" + 
				"void main()\n" + 
				"{\n" + 
				"    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);\n" + 
				"    outTexCoord = texCoord;\n" + 
				"}";
	}
	public static String getFragment() {
		return "#version 330\n" + 
				"\n" + 
				"in  vec2 outTexCoord;\n" + 
				"out vec4 fragColor;\n" + 
				"\n" + 
				"uniform sampler2D texture_sampler;\n" + 
				"\n" + 
				"void main()\n" + 
				"{\n" + 
				"    fragColor = texture(texture_sampler, outTexCoord);\n" + 
				"}";
	}

}
