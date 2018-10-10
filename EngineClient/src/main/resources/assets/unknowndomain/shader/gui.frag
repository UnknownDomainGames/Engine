#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texImage;
uniform bool usingAlpha;

uniform vec2 windowSize;
uniform vec4 clipRect;

void main()
{
    if(gl_FragCoord.x < clipRect.x || gl_FragCoord.x > clipRect.z || gl_FragCoord.y > (windowSize.y - clipRect.y) || gl_FragCoord.y < (windowSize.y - clipRect.w)) {
        discard;
    }
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