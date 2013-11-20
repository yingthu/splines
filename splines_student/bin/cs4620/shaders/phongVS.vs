#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;
uniform float un_Shininess;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

varying vec3 ex_Color;

void main(void)
{
	vec3 lightPosition = vec3(5, 5, 5);
	vec3 lightIntensity = vec3(1, 1, 1);
	vec3 lightAmbientIntensity = vec3(1, 1, 1);
	
	vec3 normal = normalize(un_NormalMatrix * in_Normal);

	vec4 eyeSpacePosition = un_ModelView * vec4(in_Vertex, 1.0);
	gl_Position = un_Projection * eyeSpacePosition;
	
	vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitToEye = normalize(-eyeSpacePosition.xyz);
	vec3 unitHalfVec = vec3(0.0,0.0,0.0);

	vec3 unitNormal = normalize(normal);
	ex_Color = un_AmbientColor * lightAmbientIntensity;
	float nDotL = 0.0;
	float nDotH = 0.0;
	
	unitToLight = normalize((un_ModelView * vec4(lightPosition, 1)).xyz - eyeSpacePosition.xyz);
	unitHalfVec = normalize(unitToLight + unitToEye);
	nDotL = dot(unitNormal, unitToLight);
	nDotH = dot(unitNormal, unitHalfVec);

	ex_Color = ex_Color + lightIntensity *
		( un_DiffuseColor * max(0.0, nDotL)
		+ un_SpecularColor * pow(max(0.0, nDotH), un_Shininess) * step(0.0, nDotL)
		);
}

