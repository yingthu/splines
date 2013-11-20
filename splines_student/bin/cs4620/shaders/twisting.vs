#version 120

uniform mat4 un_Projection;
uniform mat4 un_ModelView;

uniform float un_Twist;

attribute vec3 in_Vertex;

void main()
{
    float angle = un_Twist * length(in_Vertex.xy);
    float s = sin(angle);
    float c = cos(angle);
    gl_Position.x = c * in_Vertex.x - s * in_Vertex.y;
    gl_Position.y = s * in_Vertex.x + c * in_Vertex.y;
    gl_Position.z = 0.0;
    gl_Position.w = 1.0;
}
