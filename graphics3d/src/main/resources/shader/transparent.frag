#version 420 core

layout(early_fragment_tests) in;

uniform sampler2D u_Texture;
layout (binding = 0, std140) uniform Matrices {
    mat4 proj;
    mat4 view;
    mat4 model;
} matrices;

struct DirLight {
    vec3 color;
    float intensity;
    vec3 direction;
};

struct PointLight {
    vec3 color;
    float intensity;
    vec3 position;
    float kconstant;
    float klinear;
    float kquadratic;
};

struct SpotLight {
    vec3 color;
    float intensity;
    vec3 position;
    float kconstant;
    float klinear;
    float kquadratic;
    vec3 direction;
    float cutoffAngle;
    float outerCutoffAngle;
};

#define MAX_DIR_LIGHTS 1
#define MAX_POINT_LIGHTS 16
#define MAX_SPOT_LIGHTS 16
layout (std140) uniform Light {
    DirLight dirLights[MAX_DIR_LIGHTS];
    PointLight pointLights[MAX_POINT_LIGHTS];
    SpotLight spotLights[MAX_SPOT_LIGHTS];
    vec4 ambientLight;
} light;

layout (location = 0) in vec4 v_Color;
layout (location = 1) in vec2 v_TexCoord;
layout (location = 2) in vec3 mv_Position;
layout (location = 3) in vec3 mv_Normal;

layout (binding = 0, r32ui) uniform uimage2D linkedListHeader;
layout (binding = 1, rgba32ui) uniform writeonly uimageBuffer linkedListBuffer;
layout (binding = 0, offset = 0) uniform atomic_uint listCounter;

out vec4 fragColor;

vec4 texColor;

const float reflectance = 0.2;
const float specularPower = 10;

vec4 computeLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 lightDirection, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    vec3 to_light_src_dir = -normalize(lightDirection);
    // 漫反射光
    float diffuseFactor = max(dot(normal, to_light_src_dir), 0.0);
    diffuseColor = texColor * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    //    return diffuseColor;
    mat3 tmp;
    tmp[0] = matrices.view[0].xyz;
    tmp[1] = matrices.view[1].xyz;
    tmp[2] = matrices.view[2].xyz;
    vec3 camera_position = inverse(tmp) * -vec3(matrices.view[3].xyz);
    vec3 frag_position = (inverse(matrices.view) * vec4(mv_Position, 1.0)).xyz;

    // 镜面反射光
    vec3 camera_direction = normalize(camera_position - frag_position);
    //    vec3 camera_direction = normalize(-position);
    vec3 reflected_light = normalize(reflect(to_light_src_dir, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    //    vec3 reflected_light = normalize(to_light_src_dir + camera_direction);
    //    float specularFactor = max(dot(normal, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = texColor * lightIntensity * specularFactor * reflectance * vec4(lightColor, 1.0);

    return diffuseColor + specularColor;
}

vec4 computeDirLight(DirLight light, vec3 position, vec3 normal) {
    return computeLightColor(light.color, light.intensity, mv_Position, light.direction, mv_Normal);
}

vec4 computePointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 lightColor = computeLightColor(light.color, light.intensity, position, to_light_dir, normal);

    float distance = length(light_direction);
    float attenuationInv = light.kconstant + light.klinear * distance + light.kquadratic * distance * distance;
    return lightColor / attenuationInv;
}

vec4 computeSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.direction));

    vec4 lightColor = vec4(0, 0, 0, 0);

    if (spot_alfa > light.cutoffAngle)
    {
        vec3 light_direction = light.position - position;
        vec3 to_light_dir  = normalize(light_direction);
        vec4 light_colour = computeLightColor(light.color, light.intensity, position, to_light_dir, normal);

        float distance = length(light_direction);
        float attenuationInv = light.kconstant + light.klinear * distance + light.kquadratic * distance * distance;
        lightColor = light_colour / attenuationInv;
        lightColor *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoffAngle));
    }
    return lightColor;
}

void main() {
    texColor = v_Color * texture(u_Texture, v_TexCoord);

    vec4 lightColor = vec4(0.0, 0.0, 0.0, 0.0);

    for (int i = 0; i < MAX_DIR_LIGHTS; i++) {
        if (light.dirLights[i].intensity > 0) {
            lightColor += computeDirLight(light.dirLights[i], mv_Position, mv_Normal);
        }
    }

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (light.pointLights[i].intensity > 0) {
            lightColor += computePointLight(light.pointLights[i], mv_Position, mv_Normal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (light.spotLights[i].intensity > 0) {
            lightColor += computeSpotLight(light.spotLights[i], mv_Position, mv_Normal);
        }
    }

    fragColor = texColor * light.ambientLight + lightColor;

    uint index = atomicCounterIncrement(listCounter);

    uint oldHead = imageAtomicExchange(linkedListHeader, ivec2(gl_FragCoord.xy), uint(index));
    uvec4 item;
    item.x = oldHead;
    item.y = packUnorm4x8(fragColor);
    item.z = floatBitsToUint(gl_FragCoord.z);
    item.w = 255 / 4;

    imageStore(linkedListBuffer, int(index), item);
    discard;
}