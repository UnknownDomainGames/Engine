#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texImage;
uniform vec4 color;
uniform bool usingAlpha;
uniform bool usingTex;

void main()
{
    if(usingAlpha){
        fragColor = vec4(vertexColor.rgb, texture2D(texImage, textureCoord).r * vertexColor.a);
    }
    else{
        if(usingTex){
            fragColor = vertexColor * texture2D(texImage, textureCoord);
        }
        else{
            fragColor = vertexColor;
        }
    }
}