#version 330 core

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_Texcoord;
//layout (location = 2) in vec3 a_Normal;
//layout (location = 3) in vec4 a_Color;

out vec2 v_UV;

//out vec3 v_Normal;
//out vec3 v_LightDirection;

void main() {
    vec4 worldPos = u_ModelMatrix * a_Position;
    gl_Position = u_ProjMatrix * u_ViewMatrix * worldPos;
    v_UV = a_Texcoord;

//    v_Color = a_Color;
//    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
//    v_Normal = normalize(normal).xyz;
//    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
