#version 330 core
out vec4 fragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;

void main()
{
    const float gamma = 2.2;
    float exposure = 1.0f; //TODO: parameter
    vec3 hdrColor = texture(screenTexture, TexCoords).rgb;
    // 曝光色调映射
    vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
    // Gamma校正
    //mapped = pow(mapped, vec3(1.0 / gamma));
    fragColor = vec4(mapped,1.0);
}