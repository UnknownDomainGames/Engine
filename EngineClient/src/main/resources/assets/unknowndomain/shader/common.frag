#version 330 core
//in vec3 v_Normal;
//in vec3 v_LightDirection;
in vec2 v_UV;
uniform sampler2D u_Texture;
out vec4 fragColor;

void main() {
    fragColor = texture(u_Texture, v_UV);
}