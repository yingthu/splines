#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;

varying vec3 ex_foo;

void main(void)
{
	gl_FragColor = vec4(0.0, ex_foo.y, 0.0, 1.0);
}

