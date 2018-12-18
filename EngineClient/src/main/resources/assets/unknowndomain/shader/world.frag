#version 330 core

uniform sampler2D u_Texture;
uniform bool u_UseColor;
uniform bool u_UseTexture;

in vec4 v_Color;
in vec2 v_UV;

//in vec3 v_Normal;
//in vec3 v_LightDirection;

out vec4 fragColor;

void main() {
    if(u_UseTexture) {
        fragColor = texture(u_Texture, v_UV);
    } else if(u_UseColor) {
        fragColor = v_Color;
    }
}