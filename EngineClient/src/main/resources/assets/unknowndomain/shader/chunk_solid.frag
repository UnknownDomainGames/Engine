#version 330 core

uniform sampler2D u_Texture;
uniform bool useDirectUV = true;

in vec4 v_Color;
in vec2 v_TexCoord;

in vec3 v_Normal;
in vec3 v_FragPos;
in vec3 v_viewPos;
//in vec3 v_LightDirection;

out vec4 fragColor;

struct Light {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct DirLight {
    vec3 direction;

    Light light;
};

struct PointLight{
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    Light light;
};

struct SpotLight{
    vec3 position;
    vec3 direction;
    float cutoffCosine;
    float outerCutoff;

    float constant;
    float linear;
    float quadratic;

    Light light;
};

struct Material {
    vec3 ambient;
    vec3 diffuseColor;
    sampler2D diffuse;
    bool diffuseUseUV;
    vec3 specularColor;
    sampler2D specular;
    bool specularUseUV;

    float shininess;
};

#define N_DIR_LIGHTS 4
uniform DirLight dirLights[N_DIR_LIGHTS];
#define N_POINT_LIGHTS 16
uniform PointLight pointLights[N_POINT_LIGHTS];
#define N_SPOT_LIGHTS 10
uniform SpotLight spotLights[N_SPOT_LIGHTS];

uniform Material material;

vec3 calcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.direction);
    // 漫反射着色
    float diff = max(dot(normal, lightDir), 0.0);
    // 镜面光着色
    //vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(viewDir, halfwayDir), 0.0), material.shininess);
    // 合并结果
    vec3 ambient = vec3(1);
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    if(material.diffuseUseUV){
        ambient  = light.light.ambient  * material.ambient * vec3(texture(material.diffuse, v_TexCoord));
        diffuse  = light.light.diffuse  * diff * material.diffuseColor * vec3(texture(material.diffuse, v_TexCoord));
    }
    else{
        ambient  = light.light.ambient  * material.ambient;
        diffuse  = light.light.diffuse  * diff * material.diffuseColor;
    }
    if(material.specularUseUV){
        specular = light.light.specular * spec * material.specularColor * vec3(texture(material.specular, v_TexCoord));
    }
    else{
        specular = light.light.specular * 0.5f * material.specularColor;
    }
    return max(ambient + diffuse + specular,vec3(0));
}

vec3 calcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // 漫反射着色
    float diff = max(dot(normal, lightDir), 0.0);
    // 镜面光着色
    //vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(viewDir, halfwayDir), 0.0), material.shininess);
    // 衰减
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
                 light.quadratic * (distance * distance));
    // 合并结果
    vec3 ambient = vec3(1);
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    if(material.diffuseUseUV){
        ambient  = light.light.ambient  * material.ambient* vec3(texture(material.diffuse, v_TexCoord));
        diffuse  = light.light.diffuse  * diff * material.diffuseColor * vec3(texture(material.diffuse, v_TexCoord));
    }
    else{
        ambient  = light.light.ambient  * material.ambient;
        diffuse  = light.light.diffuse  * diff * material.diffuseColor;
    }
    if(material.specularUseUV){
        specular = light.light.specular * spec * material.specularColor * vec3(texture(material.specular, v_TexCoord));
    }
    else{
        specular = light.light.specular * spec * material.specularColor;
    }
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return max(ambient + diffuse + specular,vec3(0));
}

vec3 calcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir){
    vec3 lightDir = normalize(light.position - fragPos);
    float theta = dot(lightDir,normalize(-light.direction));
    float epsilon   = light.cutoffCosine - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    // diffuse
    vec3 norm = normalize(normal);
    float diff = max(dot(norm, lightDir), 0.0);

    // specular
    //vec3 reflectDir = reflect(-lightDir, norm);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(viewDir, halfwayDir), 0.0), material.shininess);

    vec3 ambient = vec3(1);
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    if(material.diffuseUseUV){
        ambient  = light.light.ambient  * material.ambient* vec3(texture(material.diffuse, v_TexCoord));
        diffuse  = light.light.diffuse  * diff * material.diffuseColor* vec3(texture(material.diffuse, v_TexCoord));
    }
    else{
        ambient  = light.light.ambient  * material.ambient;
        diffuse  = light.light.diffuse  * diff * material.diffuseColor;
    }
    if(material.specularUseUV){
        specular = light.light.specular * spec * material.specularColor* vec3(texture(material.specular, v_TexCoord));
    }
    else{
        specular = light.light.specular * spec * material.specularColor;
    }
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    //ambient  *= attenuation; // remove attenuation from ambient, as otherwise at large distances the light would be darker inside than outside the spotlight due the ambient term in the else branche
    diffuse  *= attenuation * intensity;
    specular *= attenuation * intensity;

    return max(ambient + diffuse + specular,vec3(0));

}

void main() {
    vec3 norm = normalize(v_Normal);
    vec3 viewDir = normalize(v_viewPos - v_FragPos);

    vec3 result = vec3(0f);
    for (int i = 0; i < N_DIR_LIGHTS;i++){
        result += calcDirLight(dirLights[i], norm, viewDir);
    }
    for (int i = 0; i < N_POINT_LIGHTS;i++){
        result += calcPointLight(pointLights[i], norm, v_FragPos, viewDir);
    }
    for (int i = 0; i < N_SPOT_LIGHTS;i++){
        result += calcSpotLight(spotLights[i], norm, v_FragPos, viewDir);
    }
    if(useDirectUV){
    //fragColor = vec4(1.0);
        fragColor = vec4(result,1.0) /* * v_Color */ * texture(u_Texture, v_TexCoord);
    }
    else {
        fragColor = vec4(result,1.0);
    }
}