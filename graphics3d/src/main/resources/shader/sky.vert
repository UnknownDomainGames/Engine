#version 420 core

layout (binding = 0, std140) uniform Transformation {
    mat4 projMatrix;
    mat4 viewMatrix;
} matrices;

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;

layout (location = 0) out vec4 v_Color;
layout (location = 1) out vec2 v_TexCoord;

void main() {
    vec4 vs_Position = matrices.viewMatrix * vec4(a_Position, 0.0);
    gl_Position = matrices.projMatrix * vec4(vs_Position.xyz, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
}
