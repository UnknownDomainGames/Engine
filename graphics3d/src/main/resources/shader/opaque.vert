#version 420 core

layout (binding = 0, std140) uniform Matrices {
    mat4 proj;
    mat4 view;
    mat4 model;
} matrices;

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;
layout (location = 3) in vec3 a_Normal;

layout (location = 0) out vec4 v_Color;
layout (location = 1) out vec2 v_TexCoord;
layout (location = 2) out vec3 mv_Position;
layout (location = 3) out vec3 mv_Normal;

void main() {
    vec4 mv_Position4 = matrices.view * matrices.model * vec4(a_Position, 1.0);
    v_Color = a_Color;
    v_TexCoord = a_TexCoord;
    mv_Position = mv_Position4.xyz;
    mv_Normal = normalize(matrices.view * matrices.model * vec4(a_Normal, 0.0)).xyz;
    gl_Position = matrices.proj * mv_Position4;
}
