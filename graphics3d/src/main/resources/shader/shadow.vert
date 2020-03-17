#version 330

uniform Matrices {
    mat4 proj;
    mat4 viewModel;
} matrices;

layout (location = 0) in vec3 a_Position;

void main() {
    gl_Position = matrices.proj * matrices.viewModel * vec4(a_Position, 1.0);
}