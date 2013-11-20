#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;

varying vec3 ex_Color;

void main(void)	
{
	gl_FragColor = vec4(ex_Color, 1);
}

