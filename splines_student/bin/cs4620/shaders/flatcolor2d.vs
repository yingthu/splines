#version 120

// uniforms -- same value is used for every vertex in model
uniform mat3 un_Projection;
uniform mat3 un_ModelView;

uniform vec3 un_Color;

// vertex attributes -- distinct value used for each vertex
attribute vec2 in_Vertex;

void main(void)
{
	vec3 transformed = un_Projection * un_ModelView * vec3(in_Vertex, 1.0);
	gl_Position = vec4(transformed.xy, 0.0, 1.0);
}

