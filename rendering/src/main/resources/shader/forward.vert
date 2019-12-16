#version 330 core

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;
uniform mat4 u_LightSpace;
uniform vec3 u_viewPos;

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TexCoord;
layout (location = 3) in vec3 a_Normal;
layout (location = 4) in vec3 a_Tangent;

out vec4 v_Color;
out vec2 v_TexCoord;

out vec3 v_Normal;
out vec3 v_FragPos;
out vec4 v_FragPosLightSpace;
out vec3 v_viewPos;
out mat3 v_TBN;
//out vec3 v_LightDirection;

void main() {
    v_FragPos = (u_ModelMatrix * vec4(a_Position.xyz, 1.0)).xyz;

    gl_Position = u_ProjMatrix * u_ViewMatrix * vec4(v_FragPos, 1.0);
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;

    v_Normal = (u_ViewMatrix * u_ModelMatrix * vec4(a_Normal, 0.0)).xyz;
    v_viewPos = /*-u_ViewMatrix[3].xyz*/ u_viewPos;

    vec3 T = normalize(vec3(u_ModelMatrix * vec4(a_Tangent, 0.0)));
    vec3 N = normalize(vec3(u_ModelMatrix * vec4(a_Normal, 0.0)));

    T = normalize(T - dot(T, N) * N);
    vec3 B = cross(T, N);
    v_TBN = transpose(mat3(T, B, N));
    v_FragPosLightSpace = u_LightSpace * vec4(v_FragPos, 1.0f);
    //    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
    //    v_Normal = normalize(normal).xyz;
    //    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
