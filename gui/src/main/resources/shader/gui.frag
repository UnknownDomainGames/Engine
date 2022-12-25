#version 330 core

uniform bool renderText;
uniform bool enableGamma;
uniform sampler2D u_Texture;

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;
const float gamma = 2.2;

void main()
{
    if (renderText) {
        fragColor = vec4(v_Color.rgb, texture(u_Texture, v_TexCoord).r * v_Color.a);
    } else {
        fragColor = v_Color * texture(u_Texture, v_TexCoord);
    }

    if (enableGamma) {
        fragColor = vec4(pow(fragColor.rgb, vec3(1.0 / gamma)), fragColor.a);
    }
}