#version 330

uniform mat4 projMatrix;
uniform mat4 viewModelMatrix;

layout (location = 0) in vec3 a_Position;

void main() {
    gl_Position = projMatrix * viewModelMatrix * vec4(a_Position, 1.0);
}