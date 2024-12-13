#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int StarLayers;

in vec4 texProj0;
in vec2 texCoord0;

const mat4 SCALE_TRANSLATE = mat4(
0.5, 0.0, 0.0, 0.5,
0.0, 0.5, 0.0, 0.5,
0.0, 0.0, 1, 0.0,
0.0, 0.0, 0.0, 1
);

mat4 starLayer(float layer) {
    mat4 translate = mat4(
    1.0, 0.0, 0.0, 17.0 / layer,
    0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2((4.5 - layer / 4.0) * (2.0 / 3.0));

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    float mask = texture(Sampler1, texCoord0).a;
    if (mask > 0) {
        vec3 color = vec3(0, 0, 0);
        for (int i = 0; i < StarLayers; i++) {
            color += textureProj(Sampler0, texProj0 * starLayer(float(i + 1))).rgb;
        }
        fragColor = vec4(color, 1.0);
    } else {
        discard;
    }
}
