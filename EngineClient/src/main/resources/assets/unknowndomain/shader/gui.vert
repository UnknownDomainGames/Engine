#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 vertexNormal;

out vec4 vertexColor;
out vec2 textureCoord;

uniform mat4 projection;
uniform mat4 model;
uniform vec4 clipRect;

void main()
{
    gl_Position = projection * model * vec4(position.x + clipRect.x, position.y + clipRect.y, position.z, 1.0); //projection * view * model * vec4(position, 1.0);
    vertexColor = color;
    textureCoord = texCoord;
}