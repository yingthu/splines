#version 120

uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform vec3 un_DiffuseColor;
uniform float un_Twist;

void main()
{
    gl_FragColor = vec4(un_DiffuseColor, 1);
}
