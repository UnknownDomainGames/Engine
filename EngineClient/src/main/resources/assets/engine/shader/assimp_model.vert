#version 330 core

const int MAX_BONES = 251;

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjMatrix;
uniform mat4 u_ModelMatrix;
uniform mat4 u_LightSpace;
uniform vec3 u_viewPos;
uniform mat4 u_Bones[MAX_BONES];

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_TexCoord;
layout (location = 2) in vec3 a_Normal;
layout (location = 3) in vec3 a_Tangent;
layout (location = 4) in ivec4 a_BoneId;
layout (location = 5) in vec4 a_Weight;

out vec4 v_Color;
out vec2 v_TexCoord;

out vec3 v_Normal;
out vec3 v_FragPos;
out vec4 v_FragPosLightSpace;
out vec3 v_viewPos;
out mat3 v_TBN;
//out vec3 v_LightDirection;

void main() {
    vec4 initPos = vec4(0, 0, 0, 1);
    vec4 initNormal = vec4(0, 0, 0, 0);
    int count = 0;
    for(int i = 0; i < 4; i++)
    {
        float weight = a_Weight[i];
        if(weight > 0) {
            count++;
            int jointIndex = int(a_BoneId[i]);
            vec4 tmpPos = u_Bones[jointIndex] * vec4(a_Position.xyz, 1.0);
            initPos = initPos + 4 * weight * tmpPos;

            vec4 tmpNormal = u_Bones[jointIndex] * vec4(a_Normal, 0.0);
            initNormal += weight * tmpNormal;
        }
    }
    if (count == 0)
    {
        initPos = vec4(a_Position.xyz, 1.0);
        initNormal = vec4(a_Normal, 0.0);
    }
    else{
        initPos = initPos / 4;
    }
//    mat4 BoneTransform = u_Bones[a_BoneId[0]] * a_Weight[0];
//    BoneTransform += u_Bones[a_BoneId[1]] * a_Weight[1];
//    BoneTransform += u_Bones[a_BoneId[2]] * a_Weight[2];
//    BoneTransform += u_Bones[a_BoneId[3]] * a_Weight[3];
//
//    vec4 posL = BoneTransform * vec4(a_Position.xyz, 1.0);

    v_FragPos = (u_ModelMatrix * initPos).xyz;

    gl_Position = u_ProjMatrix * u_ViewMatrix * vec4(v_FragPos, 1.0);
    v_TexCoord = a_TexCoord;

    v_Normal = (u_ViewMatrix * u_ModelMatrix * /*BoneTransform * vec4(a_Normal,0.0)*/initNormal).xyz;
    v_viewPos = /*-u_ViewMatrix[3].xyz*/ u_viewPos;

    vec3 T = normalize(vec3(u_ModelMatrix * vec4(a_Tangent,0.0)));
    vec3 N = normalize(vec3(u_ModelMatrix * vec4(a_Normal,0.0)));

    T = normalize(T - dot(T,N) * N);
    vec3 B = cross(T,N);
    v_TBN = transpose(mat3(T,B,N));
    v_FragPosLightSpace = u_LightSpace * vec4(v_FragPos,1.0f);
//    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
//    v_Normal = normalize(normal).xyz;
//    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
