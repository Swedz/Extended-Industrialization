#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform float GameTime;
uniform float PlasmaScale;
uniform float PlasmaSpeed;

in vec2 texCoord0;

out vec4 fragColor;

vec2 transformUV(vec2 uv)
{
	vec2 transformedUV = uv;
	transformedUV *= PlasmaScale;
	vec2 translation = vec2(GameTime) * PlasmaSpeed;
	transformedUV += translation;
	return fract(transformedUV);
}

void main()
{
	vec4 plasmaTexture = texture(Sampler0, transformUV(texCoord0));
	if (plasmaTexture.a > 0)
	{
		fragColor = vec4(plasmaTexture.rgb, 0.8);
	}
	else
	{
		discard;
	}
}