#version 330 core
​
in vec3 position;
in vec2 texCoord;
in vec3 vertexNormal;
​
out vec2 textureCoord;
​

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
​
void main()
{
    gl_Position = projection * model * vec4(position, 1.0);
    textureCoord = texCoord;
}