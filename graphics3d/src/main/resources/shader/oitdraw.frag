#version 420 core

layout (binding = 0, r32ui) uniform uimage2D linkedListHeader;
layout (binding = 1, rgba32ui) uniform uimageBuffer linkedListBuffer;

out vec4 fragColor;

#define MAX_FRAGMENT 40

void main() {
    uint index = imageLoad(linkedListHeader, ivec2(gl_FragCoord).xy).x;
    uint fragCount = 0;
    uvec4 fragList[MAX_FRAGMENT];

    // retrieve all the fragments in this pixel
    while (index != 0 && fragCount < MAX_FRAGMENT) {
        uvec4 frag = imageLoad(linkedListBuffer, int(index));
        fragList[fragCount] = frag;
        index = frag.x;
        fragCount++;
    }

    if (fragCount == 0) discard;// No fragments in this pixel, skip

    if (fragCount > 1) {
        // sort the fragment by its depth, in descending order
        // as the concept of rendering transparent/translucent fragments
        // is to render the furthest fragment first.
        for (uint i = 0; i < fragCount - 1;i++) {
            for (uint j = i + 1; j < fragCount;j++) {
                uvec4 frag1 = fragList[i];
                uvec4 frag2 = fragList[j];

                float depth1 = uintBitsToFloat(frag1.z);
                float depth2 = uintBitsToFloat(frag2.z);

                if (depth1 < depth2){
                    fragList[i] = frag2;
                    fragList[j] = frag1;
                }
            }
        }
    }

    vec4 color = unpackUnorm4x8(fragList[0].y);
    float depth = uintBitsToFloat(fragList[fragCount - 1].z);

    for (uint i = 1; i < fragCount; i++)
    {
        vec4 partialColor = unpackUnorm4x8(fragList[i].y);
        color = mix(color, partialColor, partialColor.a/* + fragList[i].w / 255*/);
    }

    fragColor = color;
    gl_FragDepth = depth;
}