#version 120

// uniforms -- same value is used for every vertex in model
uniform mat3 un_Projection;
uniform mat3 un_ModelView;

// varying variables -- values to be interpolated per-fragment
varying vec3 ex_Color;

void main(void)
{
	// apply interpolated color
	gl_FragColor = vec4(ex_Color, 1.0);
}

