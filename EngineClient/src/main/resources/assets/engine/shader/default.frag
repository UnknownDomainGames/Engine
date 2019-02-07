#version 330 core

uniform sampler2D u_Texture;

in vec4 v_Color;
in vec2 v_TexCoord;

in vec3 v_Normal;
in vec3 v_FragPos;
//in vec3 v_LightDirection;

uniform vec3 ambientColor;
uniform vec3 diffuseColor;
uniform vec3 specularColor;

out vec4 fragColor;

void main() {
    vec3 norm = normalize(v_Normal);
    vec3 lightDir = normalize(-vec3(-0.15f,-1.0f,-0.35f));
    //vec3 lightcolor = vec3(1.0,1.0,1.0);
    vec3 ambient = vec3(0.5 * ambientColor);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColor;
    fragColor = /*vec4(ambient + diffuse,1.0) * */texture(u_Texture, v_TexCoord);
}