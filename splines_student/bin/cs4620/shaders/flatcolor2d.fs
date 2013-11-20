#version 120

// uniforms -- same value is used for every vertex in model
uniform mat3 un_Projection;
uniform mat3 un_ModelView;

uniform vec3 un_Color;

void main(void)
{
	// apply diffuse color
	gl_FragColor = vec4(un_Color, 1.0);
}

