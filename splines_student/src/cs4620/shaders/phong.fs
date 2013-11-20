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

// TODO: (Shaders 1 Problem 1) Specify any varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main(void)
{
	// TODO: (Shaders 1 Problem 1) Implement the fragment shader for per-pixel
	// Blinn-Phong here
	vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitToEye = normalize(-ex_EyeSpacePosition.xyz);
	vec3 unitHalfVec = vec3(0.0,0.0,0.0);
	vec3 unitNormal = normalize(ex_Normal);
	
	// La = ka Ia
	vec3 colorRGB = un_AmbientColor * un_LightAmbientIntensity;
	float nDotL = 0.0;
	float nDotH = 0.0;
	for (int i = 0; i < 16; i++)
	{
		unitToLight = normalize(un_LightPositions[i] - ex_EyeSpacePosition.xyz);
		unitHalfVec = normalize(unitToLight + unitToEye);
		nDotL = dot(unitNormal, unitToLight);
		nDotH = dot(unitNormal, unitHalfVec);
		
		// Diffuse and specular contribute only when n.l > 0
		colorRGB = colorRGB + un_LightIntensities[i] *
			( un_DiffuseColor * max(0.0, nDotL)
			+ un_SpecularColor * pow(max(0.0, nDotH), un_Shininess) * step(0.0, nDotL)
			);
	}
	gl_FragColor = vec4(colorRGB, 1);
}

