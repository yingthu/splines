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

varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main(void)
{
	ex_Normal = normalize(un_NormalMatrix * in_Normal);

	ex_EyeSpacePosition = un_ModelView * vec4(in_Vertex, 1.0);
	gl_Position = un_Projection * ex_EyeSpacePosition;
}

