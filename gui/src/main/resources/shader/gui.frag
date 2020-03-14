#version 330 core

layout (std140) uniform States {
    mat4 projMatrix;
    mat4 modelMatrix;
    vec4 clipRect;
    bool renderText;
    bool enableGamma;
} states;
uniform sampler2D u_Texture;

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;
const float gamma = 2.2;

void main()
{
    if (states.renderText) {
        fragColor = vec4(v_Color.rgb, texture(u_Texture, v_TexCoord).r * v_Color.a);
    } else {
        fragColor = v_Color * texture(u_Texture, v_TexCoord);
    }

    if (states.enableGamma) {
        fragColor = vec4(pow(fragColor.rgb, vec3(1.0 / gamma)), fragColor.a);
    }
}