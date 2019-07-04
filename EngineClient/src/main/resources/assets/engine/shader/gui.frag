#version 330 core

uniform sampler2D u_Texture;
uniform bool u_RenderText;
uniform bool u_EnableGamma;

uniform vec2 u_WindowSize;
uniform vec4 u_ClipRect;

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;
const float gamma = 2.2;

void main()
{
    if(gl_FragCoord.x < u_ClipRect.x || gl_FragCoord.x > u_ClipRect.z || gl_FragCoord.y > (u_WindowSize.y - u_ClipRect.y) || gl_FragCoord.y < (u_WindowSize.y - u_ClipRect.w)) {
        discard;
    }
    if(u_RenderText) {
        fragColor = vec4(v_Color.rgb, texture(u_Texture, v_TexCoord).r * v_Color.a);
    } else {
        fragColor = v_Color * texture(u_Texture, v_TexCoord);
    }

    if(u_EnableGamma){
        fragColor = vec4(pow(fragColor.rgb, vec3(1.0 / gamma)), fragColor.a);
    }
}