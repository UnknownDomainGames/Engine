#version 330 core

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;

out vec4 v_Color;
out vec2 v_TexCoord;

//out vec3 v_Normal;
//out vec3 v_LightDirection;

void main() {
    vec4 worldPos = u_ModelMatrix * a_Position;
    gl_Position = u_ProjMatrix * u_ViewMatrix * worldPos;
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;

//    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
//    v_Normal = normalize(normal).xyz;
//    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
