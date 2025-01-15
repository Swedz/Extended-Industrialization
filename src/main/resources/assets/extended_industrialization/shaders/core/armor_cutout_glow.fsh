#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main()
{
	vec4 textureColor = texture(Sampler0, texCoord0);
	if (textureColor.a == 0.0)
	{
		discard;
	}
	vec3 color = textureColor.rgb * vertexColor.rgb;
	fragColor = vec4(color, textureColor.a);
}
