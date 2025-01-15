#version 150

#moj_import <projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;

out vec2 texCoord0;
out vec4 shaderColor;

void main()
{
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1);
	texCoord0 = (TextureMat * vec4(UV0, 0, 1)).xy;
	shaderColor = Color;
}
