#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 vertexNormal;

out vec4 vertexColor;
out vec2 textureCoord;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;


layout(std140) uniform VertexStatus{
    bool usepos;
    bool usecolor;
    bool usetex;
    bool usenormal;
};

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);
    vertexColor = color;
    textureCoord = texCoord;
}