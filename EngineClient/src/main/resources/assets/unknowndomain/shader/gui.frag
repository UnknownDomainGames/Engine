#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texImage;
uniform bool usingAlpha;

void main()
{
    if(usingAlpha){
        fragColor = vec4(vertexColor.rgb, texture(texImage, textureCoord).r * vertexColor.a);
    }
    else{
//        if(tex){
            fragColor = vertexColor * texture(texImage, textureCoord);
//        }
//        else{
//            fragColor = vertexColor;
//        }
    }
}