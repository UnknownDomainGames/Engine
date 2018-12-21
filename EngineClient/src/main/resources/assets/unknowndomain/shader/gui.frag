#version 330 core

in vec4 v_Color;
in vec2 v_TexCoord;

out vec4 fragColor;

uniform sampler2D u_Texture;
uniform bool u_UsingAlpha;
uniform bool u_UsingTexture;

uniform vec2 u_WindowSize;
uniform vec4 u_ClipRect;

void main()
{
    if(gl_FragCoord.x < u_ClipRect.x || gl_FragCoord.x > u_ClipRect.z || gl_FragCoord.y > (u_WindowSize.y - u_ClipRect.y) || gl_FragCoord.y < (u_WindowSize.y - u_ClipRect.w)) {
        discard;
    }
    if(u_UsingAlpha){
        fragColor = vec4(v_Color.rgb, texture(u_Texture, v_TexCoord).r * v_Color.a);
    }
    else{
//        if(tex){
            fragColor = v_Color * texture(u_Texture, v_TexCoord);
//        }
//        else{
//            fragColor = vertexColor;
//        }
    }
}