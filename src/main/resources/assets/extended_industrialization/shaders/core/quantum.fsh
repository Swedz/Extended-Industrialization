#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
// The quantum star textures
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform float GameTime;
uniform int StarLayers;

in vec2 texCoord0;
in vec4 shaderColor;

const int stars[10] = int[](4, 4, 8, 5, 4, 4, 6, 4, 7, 3);
const int starsTime[10] = int[](1200, 1200, 600, 1200, 1200, 1200, 1200, 1200, 1200, 1200);

float starCount(int layer)
{
	return float(stars[layer]);
}

vec2 starUV(int layer)
{
	return vec2(1.0, 1.0 / starCount(layer));
}

float starState(int layer)
{
	float starCount = starCount(layer);
	return round(fract(GameTime * starsTime[layer]) * starCount);
}

vec2 transformUV(vec2 uv, int layer, float state)
{
	vec2 starUV = starUV(layer);
	vec2 transformedUV = uv * starUV * 2;
	vec2 translation = vec2(0, GameTime * 20);
	transformedUV += translation;
	transformedUV = mod(transformedUV, starUV);
	transformedUV.y += state / starCount(layer);
	return fract(transformedUV);
}

vec3 starTexture(int layer, vec2 uv)
{
	if (layer == 0) return texture(Sampler1, uv).rgb;
	if (layer == 1) return texture(Sampler2, uv).rgb;
	if (layer == 2) return texture(Sampler3, uv).rgb;
	if (layer == 3) return texture(Sampler4, uv).rgb;
	if (layer == 4) return texture(Sampler5, uv).rgb;
	return vec3(0.0);
}

out vec4 fragColor;

void main()
{
	float mask = texture(Sampler0, texCoord0).a;
	if (mask > 0)
	{
		vec3 color = vec3(0, 0, 0);
		for (int layer = 0; layer < StarLayers; layer++)
		{
			color += starTexture(layer, transformUV(texCoord0, layer, starState(layer)));
		}
		color *= shaderColor.rgb;
		fragColor = vec4(color, 1.0);
	}
	else
	{
		discard;
	}
}
