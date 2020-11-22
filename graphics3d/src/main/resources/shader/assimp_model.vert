#version 420 core

const int MAX_BONES = 300;
const int MAX_WEIGHTS = 4;

layout (binding = 0, std140) uniform Matrices {
    mat4 proj;
    mat4 view;
    mat4 model;
} matrices;
layout (binding = 1, std140) uniform Bones {
    mat4 bones[MAX_BONES];
};

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec2 a_TexCoord;
layout (location = 2) in vec3 a_Normal;
layout (location = 3) in vec3 a_Tangent;
layout (location = 4) in vec4 a_Weight;
layout (location = 5) in ivec4 a_BoneId;

//out vec4 v_Color;
//out vec2 v_TexCoord;
//
//out vec3 v_Normal;
//out vec3 v_FragPos;
//out vec4 v_FragPosLightSpace;
//out vec3 v_viewPos;
//out mat3 v_TBN;
//out vec3 v_LightDirection;
layout (location = 0) out vec4 v_Color;
layout (location = 1) out vec2 v_TexCoord;
layout (location = 2) out vec3 mv_Position;
layout (location = 3) out vec3 mv_Normal;

void main() {
    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    //    mat4 matBone = mat4(0);
    int count = 0;
    for (int i = 0; i < MAX_WEIGHTS; i++)
    {
        float weight = a_Weight[i];
        //        matBone += weight * bones.bone[jointIndex];
        //        float weight = a_Weight[0];
        if (weight > 0) {
            count++;

            int jointIndex = a_BoneId[i];
            vec4 tmpPos = bones[jointIndex] * vec4(a_Position, 1.0);
            //            vec4 tmpPos = mat4(1.0) * vec4(a_Position, 1.0);
            initPos += weight * tmpPos;
            //            initPos = initPos + weight * mat4(0.0) * vec4(a_Position, 1.0);

            vec4 tmpNormal = bones[jointIndex] * vec4(a_Normal, 0.0);
            //            vec4 tmpNormal = mat4(1.0) * vec4(a_Normal, 0.0);
            initNormal += weight * tmpNormal;
            //            initNormal += weight * mat4(0.0) * vec4(a_Normal, 0.0);
        }
    }
    if (count == 0)
    {
        initPos = vec4(a_Position, 1.0);
        initNormal = vec4(a_Normal, 0.0);
    }
    //    initPos = matBone * vec4(a_Position, 1.0);
    //    initNormal = matBone * vec4(a_Normal, 0.0);
    //    mat4 BoneTransform = u_Bones[a_BoneId[0]] * a_Weight[0];
    //    BoneTransform += u_Bones[a_BoneId[1]] * a_Weight[1];
    //    BoneTransform += u_Bones[a_BoneId[2]] * a_Weight[2];
    //    BoneTransform += u_Bones[a_BoneId[3]] * a_Weight[3];
    //
    //    vec4 posL = BoneTransform * vec4(a_Position, 1.0);

    vec4 mv_Position4 = matrices.view * matrices.model * initPos;
    v_Color = vec4(1.0, 1.0, 1.0, 1.0);
    v_TexCoord = a_TexCoord;
    mv_Position = mv_Position4.xyz;
    mv_Normal = normalize(matrices.view * matrices.model * vec4(initNormal.xyz, 0.0)).xyz;
    gl_Position = matrices.proj * mv_Position4;

    //    v_FragPos = (u_ModelMatrix * initPos).xyz;
    //
    //    gl_Position = u_ProjMatrix * u_ViewMatrix * vec4(v_FragPos, 1.0);
    //    v_TexCoord = a_TexCoord;
    //
    //    v_Normal = (u_ViewMatrix * u_ModelMatrix * /*BoneTransform * vec4(a_Normal,0.0)*/initNormal).xyz;
    //    v_viewPos = /*-u_ViewMatrix[3].xyz*/ u_viewPos;
    //
    //    vec3 T = normalize(vec3(u_ModelMatrix * vec4(a_Tangent, 0.0)));
    //    vec3 N = normalize(vec3(u_ModelMatrix * vec4(a_Normal, 0.0)));
    //
    //    T = normalize(T - dot(T, N) * N);
    //    vec3 B = cross(T, N);
    //    v_TBN = transpose(mat3(T, B, N));
    //    v_FragPosLightSpace = u_LightSpace * vec4(v_FragPos, 1.0f);
    //    vec4 normal = u_ModelMatrix * vec4(a_Normal, 0);
    //    v_Normal = normalize(normal).xyz;
    //    v_LightDirection = normalize(u_LightPosition - worldPos.xyz);
}
