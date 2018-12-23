#version 330 core

uniform sampler2D u_Texture;
uniform bool u_UsingColor;
uniform bool u_UsingTexture;

in vec4 v_Color;
in vec2 v_TexCoord;

//in vec3 v_Normal;
//in vec3 v_LightDirection;

out vec4 fragColor;

void main() {
    if(u_UsingTexture && u_UsingColor)
    {
        fragColor = v_Color * texture(u_Texture, v_TexCoord);
    } else if(u_UsingTexture)
    {
        fragColor = texture(u_Texture, v_TexCoord);
    } else if(u_UsingColor)
    {
        fragColor = v_Color;
    }
}