#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;

out vec4 fragColor;

uniform sampler2D texImage;

layout(std140) uniform VertexStatus{
    bool pos;
    bool color;
    bool tex;
    bool normal;
};

void main() {
//    if(tex && color){
//        vec4 textureColor = texture(texImage, textureCoord);
//        fragColor = vertexColor * textureColor;
//    }
//    else if(tex){
//        fragColor = texture(texImage, textureCoord);
//    }
//    else {
//        fragColor = vertexColor;
//    }
        fragColor = vec4(1.0,1.0,1.0,1.0);
}
