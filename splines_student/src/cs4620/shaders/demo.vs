#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

varying vec3 ex_foo;

void main(void)
{
	vec3 pos = in_Vertex;
	ex_foo = pos;
	
	gl_Position = un_Projection * un_ModelView * vec4(pos, 1.0);
	
}

