#version 120

uniform mat4 un_Projection;
uniform mat4 un_ModelView;

varying vec3 ex_Color;

void main()
{
	gl_FragColor = vec4(ex_Color, 1);
}
