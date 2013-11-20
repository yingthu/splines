#version 120

uniform mat4 un_Projection;
uniform mat4 un_ModelView;

attribute vec3 in_Vertex;

void main()
{
    gl_Position = un_Projection * un_ModelView * vec4(in_Vertex, 1);
}
