#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texImage;
uniform vec4 color;

void main()
{
    fragColor = vertexColor * texture(texImage, textureCoord);
}