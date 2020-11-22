#version 420 core

#define ALPHA_THRESHOLD 0.1f

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
layout (binding=2, std140) uniform Light {
    DirLight dirLights[MAX_DIR_LIGHTS];
    PointLight pointLights[MAX_POINT_LIGHTS];
    SpotLight spotLights[MAX_SPOT_LIGHTS];
    vec4 ambientLight;
} light;

layout (binding=3, std140) uniform Material {
    bool diffuseUseUV;
    bool specularUseUV;
    bool normalUseUV;
    bool alphaUseUV;
    vec3 ambient;
    vec3 diffuseColor;
    vec3 specularColor;
    float shininess;
} material;

layout (binding=4) uniform sampler2D diffuseUV;
layout (binding=5) uniform sampler2D specularUV;
layout (binding=6) uniform sampler2D normalUV;
layout (binding=7) uniform sampler2D alphaUV;

layout (location = 0) in vec4 v_Color;
layout (location = 1) in vec2 v_TexCoord;
layout (location = 2) in vec3 mv_Position;
layout (location = 3) in vec3 mv_Normal;

out vec4 fragColor;

vec4 texColor;

const float reflectance = 0.2;
const float specularPower = 10;

//TODO: move to separate file
vec3 blendMultiply(vec3 base, vec3 blend){
    return blend * base;
}
vec3 blendMultiply(vec3 base, vec3 blend, float opacity){
    return opacity * blendMultiply(base, blend) + (1.0f-opacity) * base;
}

vec4 computeLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 lightDirection, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    vec3 to_light_src_dir = -normalize(lightDirection);
    // 漫反射光
    float diffuseFactor = max(dot(normal, to_light_src_dir), 0.0);
    if (material.diffuseUseUV){
        diffuseColor = v_Color * vec4(blendMultiply(vec3(texture(diffuseUV, v_TexCoord)), material.diffuseColor, 1.0) * lightColor * lightIntensity * diffuseFactor, 1.0);
    }
    else {
        diffuseColor = v_Color * vec4(material.diffuseColor, 1.0) * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    }
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
    if (material.specularUseUV){
        specularColor = v_Color * vec4(blendMultiply(vec3(texture(specularUV, v_TexCoord)), material.specularColor, 1.0) * lightIntensity * specularFactor * reflectance * lightColor, 1.0);
    }
    else {
        specularColor = v_Color * vec4(material.specularColor, 1.0) * lightIntensity * specularFactor * reflectance * vec4(lightColor, 1.0);
    }

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
    if (material.diffuseUseUV){
        texColor = v_Color * vec4(blendMultiply(vec3(texture(diffuseUV, v_TexCoord)), material.ambient, 1.0), 1.0);
    }
    else {
        texColor = v_Color * vec4(material.ambient, 1.0);
    }

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
    if (material.alphaUseUV){
        fragColor.a = texture(alphaUV, v_TexCoord).a;
    }
    if (fragColor.a < ALPHA_THRESHOLD){
        discard;
    }
}