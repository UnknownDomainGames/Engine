#version 330 core

uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;
uniform vec4 u_ClipRect;

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;

out vec4 v_Color;
out vec2 v_TexCoord;

void main()
{
    gl_Position = u_ProjMatrix * u_ModelMatrix * vec4(a_Position.x + u_ClipRect.x, a_Position.y + u_ClipRect.y, a_Position.z, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
}