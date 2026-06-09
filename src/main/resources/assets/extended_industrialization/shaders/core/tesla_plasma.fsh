#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3; // the block atlas, so we can scale things appropriately. For some reason Sampler1 and Sampler2 are reserved for some textures so I must use Sampler3
uniform float GameTime;
uniform float PlasmaScale;
uniform float PlasmaSpeed;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec2 transformUV(vec2 uv)
{
	vec2 textureScale = textureSize(Sampler3, 0) / 64.0;
	vec2 transformedUV = uv;
	transformedUV *= PlasmaScale;
	vec2 motion = vec2(PlasmaSpeed);
	motion /= textureScale;
	vec2 translation = vec2(GameTime) * motion;
	transformedUV += translation;
	transformedUV *= textureScale;
	return fract(transformedUV);
}

void main()
{
	vec4 plasmaTexture = texture(Sampler0, transformUV(texCoord0)) * vertexColor;
	if (plasmaTexture.a > 0)
	{
		fragColor = plasmaTexture;
	}
	else
	{
		discard;
	}
}