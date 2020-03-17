#version 420 core

uniform sampler2D u_Texture;

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

out vec4 fragColor;

vec4 texColor;

const float reflectance = 0.2;
const float specularPower = 10;

vec4 computeLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 lightDirection, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // 漫反射光
    float diffuseFactor = max(dot(normal, lightDirection), 0.0);
    diffuseColor = texColor * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    //    return diffuseColor;

    // 镜面反射光
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -lightDirection;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
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
}