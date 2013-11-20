#version 120

// uniforms -- same value is used for every vertex in model
uniform mat3 un_Projection;
uniform mat3 un_ModelView;

// vertex attributes -- distinct value used for each vertex
attribute vec2 in_Vertex;
attribute vec3 in_Color;

// varying variables -- values to be interpolated per-fragment
varying vec3 ex_Color;

void main(void)
{
	vec3 transformed = un_Projection * un_ModelView * vec3(in_Vertex, 1.0);
	ex_Color = in_Color;
	gl_Position = vec4(transformed.xy, 0.0, 1.0);
}

