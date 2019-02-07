#version 330 core

layout (location = 0) in vec3 a_Position;

uniform mat4 u_lightSpace;
uniform mat4 u_ModelMatrix;

void main() {
	gl_Position = u_lightSpace * u_ModelMatrix * vec4(a_Position, 1.0);
}
