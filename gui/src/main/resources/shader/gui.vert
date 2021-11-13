#version 330 core

layout (std140) uniform States {
    mat4 projMatrix;
    mat4 modelMatrix;
    bool renderText;
    bool enableGamma;
} states;

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;

out vec4 v_Color;
out vec2 v_TexCoord;

void main()
{
    gl_Position = states.projMatrix * states.modelMatrix * vec4(a_Position.xyz, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
}