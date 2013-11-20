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

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;
attribute vec2 in_TexCoord;

// TODO (Shaders 2 P3): Add any varying variables you need here
varying vec2 ex_TexCoord;

void main() {
	// TODO (Shaders 2 P3): Implement the fire vertex shader here
	ex_TexCoord = vec2(in_TexCoord);
	gl_Position = un_Projection * un_ModelView * vec4(in_Vertex, 1);
}