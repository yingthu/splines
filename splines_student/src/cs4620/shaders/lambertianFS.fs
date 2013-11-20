#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;

varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main(void)	
{
	vec3 lightPosition = vec3(5, 5, 5);
	vec3 lightIntensity = vec3(1, 1, 1);
	vec3 lightAmbientIntensity = vec3(1, 1, 1);

	vec3 color = un_AmbientColor * lightAmbientIntensity;
	vec3 unitToLight = normalize((un_ModelView * vec4(lightPosition, 1)).xyz - ex_EyeSpacePosition.xyz);
	
	vec3 unitNormal = normalize(ex_Normal);
	color = color + lightIntensity * un_DiffuseColor * clamp(dot(unitNormal, unitToLight), 0.0, 1.0);
	gl_FragColor = vec4(color, 1);
}

