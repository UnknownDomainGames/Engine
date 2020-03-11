#version 420 core

layout (binding = 0, std140) uniform Transformation {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 modelMatrix;
} matrices;

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;
layout (location = 3) in vec3 a_Normal;

layout (location = 0) out vec4 v_Color;
layout (location = 1) out vec2 v_TexCoord;
layout (location = 2) out vec3 v_Normal;

void main() {
    gl_Position = matrices.projMatrix * matrices.viewMatrix * matrices.modelMatrix * vec4(a_Position, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
    v_Normal = a_Normal;
}
