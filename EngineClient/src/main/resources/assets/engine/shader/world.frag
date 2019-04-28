#version 330 core

uniform sampler2D u_Texture;

in vec4 v_Color;
in vec2 v_TexCoord;

//in vec3 v_Normal;
//in vec3 v_LightDirection;

out vec4 fragColor;

void main() {
    fragColor = v_Color * texture(u_Texture, v_TexCoord);
}