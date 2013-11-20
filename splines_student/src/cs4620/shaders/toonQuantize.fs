
#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;
uniform float un_Shininess;

uniform vec3 un_LightPositions[16];
uniform vec3 un_LightIntensities[16];
uniform vec3 un_LightAmbientIntensity;

// TODO: (Shaders 1 Problem 1) Declare any varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main()
{
	// TODO: (Shaders 1 Problem 1) Implement the fragment shader for
	// the toon quantization shader here
    vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitNormal = normalize(ex_Normal);
	vec3 colorRGB = vec3(0.0,0.0,0.0);
	
	float nDotL = 0.0;
	float quantizedIntensity = 0.0;
	
	unitToLight = normalize(un_LightPositions[0] - ex_EyeSpacePosition.xyz);
	nDotL = dot(unitNormal, unitToLight);
		
	// Quantization
	if (nDotL <= 0)
		quantizedIntensity = 0.0;
	else if (nDotL <= 0.5)
		quantizedIntensity = 0.25;
	else if (nDotL <= 0.75)
		quantizedIntensity = 0.5;
	else
		quantizedIntensity = 0.75;
	
	// Diffuse color
	colorRGB += un_LightIntensities[0] * un_DiffuseColor * quantizedIntensity;
    gl_FragColor = vec4(colorRGB, 1);
}
