#version 120

uniform mat4 un_Projection;
uniform mat4 un_ModelView;

attribute vec3 in_Vertex;

varying vec3 ex_Color;

void main()
{
	gl_Position = un_Projection * un_ModelView * vec4(in_Vertex, 1);
	ex_Color = (in_Vertex.xyz + vec3(1,1,1)) * 0.5;
}
