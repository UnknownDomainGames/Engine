#version 330 core

uniform sampler2D u_Texture;
uniform sampler2D u_ShadowMap;
uniform bool useDirectUV = true;

in vec4 v_Color;
in vec2 v_TexCoord;

in vec3 v_Normal;
in vec3 v_FragPos;
in vec4 v_FragPosLightSpace;
in vec3 v_viewPos;
in mat3 v_TBN;
//in vec3 v_LightDirection;

out vec4 fragColor;

struct Light {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct DirLight {
    bool filled;
    vec3 direction;

    Light light;
};

struct PointLight{
    bool filled;
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    Light light;
};

struct SpotLight{
    bool filled;
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

    sampler2D normalUV;
    bool normalUseUV;

    sampler2D alphaUV;
    bool alphaUseUV;

    float shininess;
};

#define N_DIR_LIGHTS 4
uniform DirLight dirLights[N_DIR_LIGHTS];
#define N_POINT_LIGHTS 16
uniform PointLight pointLights[N_POINT_LIGHTS];
#define N_SPOT_LIGHTS 10
uniform SpotLight spotLights[N_SPOT_LIGHTS];

uniform Material material;

//TODO: move to separate file
vec3 blendMultiply(vec3 base, vec3 blend){
    return blend * base;
}
vec3 blendMultiply(vec3 base, vec3 blend, float opacity){
    return opacity * blendMultiply(base,blend) + (1.0f-opacity) * base;
}


void calcDirLight(DirLight light, vec3 normal, vec3 viewDir, inout vec3 ads[3])
{
    if(!light.filled){
    ads[0] = vec3(0);
    ads[1] = vec3(0);
    ads[2] = vec3(0);
    }
    else{
    vec3 lightDir = normalize(-light.direction);
    if(material.normalUseUV){
        lightDir = v_TBN * lightDir;
    }
    // 漫反射着色
    float diff = max(dot(normal, lightDir), 0.0);
    // 镜面光着色
//    vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(viewDir, halfwayDir), 0.0), material.shininess);
    // 合并结果
    vec3 ambient = vec3(0);
    vec3 diffuse = vec3(0);
    vec3 specular = vec3(0);
    if(material.diffuseUseUV){
        ambient  = light.light.ambient  * blendMultiply(vec3(texture(material.diffuse, v_TexCoord)), material.ambient,1.0);
        diffuse  = light.light.diffuse  * diff * blendMultiply(vec3(texture(material.diffuse, v_TexCoord)), material.diffuseColor,1.0);
//        ambient  = light.light.ambient  * vec3(texture(material.diffuse, v_TexCoord));
//        diffuse  = /*light.light.diffuse  * diff **/ vec3(texture(material.diffuse, v_TexCoord));
    }
    else{
        ambient  = light.light.ambient  * material.ambient;
        diffuse  = light.light.diffuse  * diff * material.diffuseColor;
    }
    if(material.specularUseUV){
        specular = light.light.specular * spec * blendMultiply(vec3(texture(material.specular, v_TexCoord)), material.specularColor,1.0);
//        specular = light.light.specular * spec * vec3(texture(material.specular, v_TexCoord));
    }
    else{
        specular = light.light.specular * spec * material.specularColor;
    }
    ads[0] = max(ambient, vec3(0));
    ads[1] = max(diffuse,vec3(0));
    ads[2] = max(specular,vec3(0));
    }
}

void calcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, inout vec3 ads[3])
{
    if(!light.filled){
    ads[0] = vec3(0);
    ads[1] = vec3(0);
    ads[2] = vec3(0);
    return;
    }
    vec3 lightDir = normalize(light.position - fragPos);
    if(material.normalUseUV){
        lightDir = v_TBN * lightDir;
    }
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
    vec3 ambient = vec3(0);
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
    ads[0] = max(ambient, vec3(0));
    ads[1] = max(diffuse,vec3(0));
    ads[2] = max(specular,vec3(0));
    //return max(ambient + diffuse + specular,vec3(0));
}

void calcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir, inout vec3 ads[3]){
    if(!light.filled){
    ads[0] = vec3(0);
    ads[1] = vec3(0);
    ads[2] = vec3(0);
    return;
    }
    vec3 lightDir = normalize(light.position - fragPos);
    if(material.normalUseUV){
        lightDir = v_TBN * lightDir;
    }

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

    ads[0] = max(ambient, vec3(0));
    ads[1] = max(diffuse,vec3(0));
    ads[2] = max(specular,vec3(0));
    //return max(ambient + diffuse + specular,vec3(0));

}

float CalcShadow(vec4 lightspaceFragPos){
    vec3 projCoords = lightspaceFragPos.xyz / lightspaceFragPos.w;
    projCoords = projCoords * 0.5 + 0.5;
    //float closetDepth = texture(u_ShadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float bias = /*max(0.05 * (1.0 - dot(normal, lightDir)), 0.005)*/ 0.005;
    vec2 texelSize = 1.0 / textureSize(u_ShadowMap, 0);
    float shadow = 0.0f;
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(u_ShadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;
    //float shadow = currentDepth - bias > closetDepth ? 1.0 : 0.0;
//    if(currentDepth > 1.0){
//        shadow = 0.0f;
//    }

    return shadow;
}

void main() {
    vec3 norm = normalize(v_Normal);
    vec3 viewDir = normalize(v_viewPos - v_FragPos);
    if(material.normalUseUV){
        norm = texture(material.normalUV, v_TexCoord).rgb;
        norm = normalize(norm * 2.0 - 1.0);

        viewDir = v_TBN * viewDir;
    }


    vec3 result[3];
    result[0] = vec3(0);
    result[1] = vec3(0);
    result[2] = vec3(0);
    vec3 tmp[3];
    tmp[0] = vec3(0);
    tmp[1] = vec3(0);
    tmp[2] = vec3(0);
    for (int i = 0; i < N_DIR_LIGHTS;i++){
        calcDirLight(dirLights[i], norm, viewDir,tmp);
        result[0] += tmp[0];
        result[1] += tmp[1];
        result[2] += tmp[2];
    }
    for (int i = 0; i < N_POINT_LIGHTS;i++){
        calcPointLight(pointLights[i], norm, v_FragPos, viewDir, tmp);
        result[0] += tmp[0];
        result[1] += tmp[1];
        result[2] += tmp[2];
    }
    for (int i = 0; i < N_SPOT_LIGHTS;i++){
        calcSpotLight(spotLights[i], norm, v_FragPos, viewDir, tmp);
        result[0] += tmp[0];
        result[1] += tmp[1];
        result[2] += tmp[2];
    }
    float alpha = 1.0f;
    if(material.alphaUseUV){
        alpha = texture(material.alphaUV,v_TexCoord).a;
    }
    float shadow = /*CalcShadow(v_FragPosLightSpace)*/0f;
    if(useDirectUV){
        fragColor = vec4(result[0] + (1f-shadow) * (result[1] + result[2]),1.0) /* * v_Color */ * texture(u_Texture, v_TexCoord);
    }
    else {
        fragColor = vec4(result[0] + (1f-shadow) * (result[1] + result[2]),1.0);
    }
    fragColor.a = alpha;
}