#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main()
{
	vec4 plasmaTexture = texture(Sampler0, texCoord0) * vertexColor;
	if (plasmaTexture.a > 0)
	{
		fragColor = plasmaTexture;
	}
	else
	{
		discard;
	}
}