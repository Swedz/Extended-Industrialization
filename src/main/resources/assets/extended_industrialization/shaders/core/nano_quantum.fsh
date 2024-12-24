#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 QuantumStarUV1;
uniform vec4 QuantumStarUV2;
uniform vec4 QuantumStarUV3;
uniform vec4 QuantumStarUV4;
uniform vec4 QuantumStarUV5;

uniform float GameTime;
uniform int QuantumStarLayers;

const vec2 starMotion[5] = vec2[](
vec2(0.0, 20.0),
vec2(0.0, 40.0),
vec2(0.0, 10.0),
vec2(0.0, 40.0),
vec2(0.0, 20.0)
);

in vec2 texCoord0;
in vec4 shaderColor;

vec4 starUV(int layer)
{
	if (layer == 0) return QuantumStarUV1;
	if (layer == 1) return QuantumStarUV2;
	if (layer == 2) return QuantumStarUV3;
	if (layer == 3) return QuantumStarUV4;
	if (layer == 4) return QuantumStarUV5;
	return vec4(0);
}

vec2 transformUV(vec2 uv, int layer)
{
	vec4 starUV = starUV(layer);
	vec2 minUV = starUV.xy;
	vec2 maxUV = starUV.zw;
	vec2 transformedUV = uv;
	transformedUV.y *= 0.5;
	vec2 translation = vec2(GameTime) * starMotion[layer];
	transformedUV += translation;
	transformedUV = mod(transformedUV - minUV, maxUV - minUV) + minUV;
	return fract(transformedUV);
}

out vec4 fragColor;

void main()
{
	float mask = texture(Sampler0, texCoord0).a;
	if (mask > 0)
	{
		vec3 color = vec3(0, 0, 0);
		for (int layer = 0; layer < QuantumStarLayers; layer++)
		{
			color += texture(Sampler1, transformUV(texCoord0, layer)).rgb;
		}
		color *= shaderColor.rgb;
		fragColor = vec4(color, 1.0);
	}
	else
	{
		discard;
	}
}
