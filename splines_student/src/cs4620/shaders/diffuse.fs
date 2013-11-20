#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;

uniform vec3 un_LightPositions[16];
uniform vec3 un_LightIntensities[16];
uniform vec3 un_LightAmbientIntensity;

varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main(void)
{
	vec3 unitToLight = vec3(0.0,0.0,0.0);

	vec3 unitNormal = normalize(ex_Normal);
	vec3 colorRGB = un_AmbientColor * un_LightAmbientIntensity;

	// for each light source
	for (int i = 0; i < 16; i++)
	{
		unitToLight = normalize(un_LightPositions[i] - ex_EyeSpacePosition.xyz);
		colorRGB = colorRGB + un_LightIntensities[i] * un_DiffuseColor * clamp(dot(unitNormal, unitToLight), 0.0, 1.0);
	}

	gl_FragColor = vec4(colorRGB, 1.0f);
}

