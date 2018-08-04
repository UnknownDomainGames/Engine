#version 330 core
//in vec3 v_Normal;
//in vec3 v_LightDirection;
in vec2 v_UV;
uniform sampler2D u_Texture;
uniform int u_Picked;
out vec4 fragColor;

void main() {
    fragColor = texture(u_Texture, v_UV);
    if (u_Picked == 1) {
        fragColor += vec4(0.5, 0, 0, 0.5);
    }
}