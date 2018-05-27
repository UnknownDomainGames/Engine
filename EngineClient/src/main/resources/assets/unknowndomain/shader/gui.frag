#version 330 core
​
in vec2 textureCoord;
in vec3 mvPos;
out vec4 fragColor;
​
uniform sampler2D texImage;
uniform vec4 color;
​
void main()
{
    fragColor = color * texture(texture_sampler, textureCoord);
}