#version 330 core

uniform sampler2D u_Texture;
uniform bool u_RenderText;
uniform bool u_EnableGamma;
uniform mat4 u_ModelMatrix;

uniform vec2 u_ViewportSize;
uniform vec4 u_ClipRect;

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;
const float gamma = 2.2;

void main()
{
    vec4 clipRect = vec4((u_ModelMatrix * vec4(u_ClipRect.xy, 0, 1)).xy, (u_ModelMatrix * vec4(u_ClipRect.zw, 0, 1)).xy);
    if (gl_FragCoord.x < clipRect.x || gl_FragCoord.x > clipRect.z ||
    gl_FragCoord.y > (u_ViewportSize.y - clipRect.y) || gl_FragCoord.y < (u_ViewportSize.y - clipRect.w)) {
        discard;
    }

    if (u_RenderText) {
        fragColor = vec4(v_Color.rgb, texture(u_Texture, v_TexCoord).r * v_Color.a);
    } else {
        fragColor = v_Color * texture(u_Texture, v_TexCoord);
    }

    if (u_EnableGamma) {
        fragColor = vec4(pow(fragColor.rgb, vec3(1.0 / gamma)), fragColor.a);
    }
}