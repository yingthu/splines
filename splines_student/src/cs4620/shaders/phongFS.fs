#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;
uniform float un_Shininess;

varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main(void)
{
	vec3 lightPosition = vec3(5, 5, 5);
	vec3 lightIntensity = vec3(1, 1, 1);
	vec3 lightAmbientIntensity = vec3(1, 1, 1);
	
	vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitToEye = normalize(-ex_EyeSpacePosition.xyz);
	vec3 unitHalfVec = vec3(0.0,0.0,0.0);

	vec3 unitNormal = normalize(ex_Normal);
	vec3 colorRGB = un_AmbientColor * lightAmbientIntensity;
	float nDotL = 0.0;
	float nDotH = 0.0;
	
	unitToLight = normalize((un_ModelView * vec4(lightPosition, 1)).xyz - ex_EyeSpacePosition.xyz);
	unitHalfVec = normalize(unitToLight + unitToEye);
	nDotL = dot(unitNormal, unitToLight);
	nDotH = dot(unitNormal, unitHalfVec);

	colorRGB = colorRGB + lightIntensity *
		( un_DiffuseColor * max(0.0, nDotL)
		+ un_SpecularColor * pow(max(0.0, nDotH), un_Shininess) * step(0.0, nDotL)
		);

	// output color
	gl_FragColor = vec4(colorRGB, 1.0f);
}

