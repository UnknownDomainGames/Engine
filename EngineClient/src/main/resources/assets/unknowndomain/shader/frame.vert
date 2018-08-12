#version 330 core

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;

layout (location = 0) in vec4 a_Position;

void main() {
	vec4 worldPos = u_ModelMatrix * a_Position;
    gl_Position = u_ProjMatrix * u_ViewMatrix * worldPos;
}
