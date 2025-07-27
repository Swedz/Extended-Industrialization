#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0; // texture mask
uniform sampler2D Sampler1; // glint overlay
uniform sampler2D Sampler2; // quantum star atlas
uniform vec4 QuantumStarUV1;
uniform vec4 QuantumStarUV2;
uniform vec4 QuantumStarUV3;
uniform vec4 QuantumStarUV4;

uniform float GameTime;
uniform int RenderStars;
uniform vec2 StarScale;
uniform int QuantumStarLayers;

const vec2 starMotion[4] = vec2[](
vec2(0, 40),
vec2(0, 30),
vec2(0, 20),
vec2(0, 30)
);

in vec2 texCoord0;
in vec4 shaderColor;

vec4 starUV(int layer)
{
	if (layer == 0) return QuantumStarUV1;
	if (layer == 1) return QuantumStarUV2;
	if (layer == 2) return QuantumStarUV3;
	if (layer == 3) return QuantumStarUV4;
	return vec4(0);
}

vec2 transformStarUV(vec2 uv, int layer)
{
	vec4 starUV = starUV(layer);
	vec2 minUV = starUV.xy;
	vec2 maxUV = starUV.zw;
	vec2 transformedUV = uv;
	transformedUV *= StarScale;
	vec2 translation = vec2(GameTime) * starMotion[layer];
	transformedUV += translation;
	transformedUV = mod(transformedUV - minUV, maxUV - minUV) + minUV;
	return fract(transformedUV);
}

vec2 transformGlintUV(vec2 uv)
{
	vec2 transformedUV = uv;
	vec2 translation = vec2(GameTime) * 15;
	transformedUV += translation;
	return fract(transformedUV);
}

out vec4 fragColor;

void main()
{
	vec4 textureColor = texture(Sampler0, texCoord0);
	float mask = textureColor.a;
	if (mask > 0)
	{
		vec3 color = textureColor.rgb;
		vec4 glintTexture = texture(Sampler1, transformGlintUV(texCoord0));
		if (glintTexture.a > 0)
		{
			color += glintTexture.rgb * shaderColor.rgb;
		}
		if (RenderStars == 1)
		{
			for (int layer = 0; layer < QuantumStarLayers; layer++)
			{
				vec4 starTexture = texture(Sampler2, transformStarUV(texCoord0, layer));
				if (starTexture.a > 0)
				{
					color = starTexture.rgb * shaderColor.rgb;
				}
			}
		}
		fragColor = vec4(color, 1.0);
	}
	else
	{
		discard;
	}
}
