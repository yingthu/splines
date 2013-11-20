#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

varying vec3 ex_Color;

void main(void)	
{
	vec3 lightPosition = vec3(5, 5, 5);
	vec3 lightIntensity = vec3(1, 1, 1);
	vec3 lightAmbientIntensity = vec3(1, 1, 1);

	vec3 unitNormal = normalize(un_NormalMatrix * in_Normal);

	vec4 eyeSpacePosition = un_ModelView * vec4(in_Vertex, 1.0);
	gl_Position = un_Projection * eyeSpacePosition;
	
	ex_Color = un_AmbientColor * lightAmbientIntensity;
	vec3 unitToLight = normalize((un_ModelView * vec4(lightPosition, 1)).xyz - eyeSpacePosition.xyz);
	
	ex_Color = ex_Color + lightIntensity * un_DiffuseColor * clamp(dot(unitNormal, unitToLight), 0.0, 1.0);
}

