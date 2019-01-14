#version 330 core

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;
uniform vec3 u_viewPos;

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;
layout (location = 3) in vec3 a_Normal;

out vec4 v_Color;
out vec2 v_TexCoord;

out vec3 v_Normal;
out vec3 v_FragPos;
out vec3 v_viewPos;
//out vec3 v_LightDirection;

void main() {
    gl_Position = u_ProjMatrix * u_ViewMatrix * u_ModelMatrix * a_Position;
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;

    v_Normal = (u_ModelMatrix * u_ViewMatrix * vec4(a_Normal,0.0)).xyz;
    v_FragPos = (u_ModelMatrix * a_Position).xyz;
    v_viewPos = /*-u_ViewMatrix[3].xyz*/ u_viewPos;
//    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
//    v_Normal = normalize(normal).xyz;
//    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
