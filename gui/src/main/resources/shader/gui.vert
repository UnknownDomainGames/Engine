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
    vec4 worldPos = u_ModelMatrix * vec4(a_Position.xyz, 1.0);
    gl_Position = u_ProjMatrix * vec4(worldPos.x + u_ClipRect.x, worldPos.y + u_ClipRect.y, a_Position.z, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
}