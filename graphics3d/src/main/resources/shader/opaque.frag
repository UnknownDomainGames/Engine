#version 420 core

uniform sampler2D u_Texture;

layout (std140) uniform DirLight {
    vec3 color;
    float intensity;
    vec3 direction;
} dirLight;

layout (location = 0) in vec4 v_Color;
layout (location = 1) in vec2 v_TexCoord;
layout (location = 2) in vec3 mv_Position;
layout (location = 3) in vec3 mv_Normal;

out vec4 fragColor;

vec4 texColor;

const float reflectance = 1;
const float specularPower = 20;

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

void main() {
    //    fragColor = v_Color;
    texColor = v_Color * texture(u_Texture, v_TexCoord);
    vec4 light = computeLightColor(dirLight.color, dirLight.intensity, mv_Position, dirLight.direction, mv_Normal);
    fragColor = texColor * vec4(0.3, 0.3, 0.3, 1.0) + light;
}