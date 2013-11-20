
#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;
uniform float un_Shininess;

uniform vec3 un_LightPositions[16];
uniform vec3 un_LightIntensities[16];
uniform vec3 un_LightAmbientIntensity;

// How far the object's surface should be displaced (in eye space)
uniform float displaceScale;

void main()
{
	// TODO: (Shaders 1 Problem 1) Implement the fragment shader for
	// the toon displacement shader here
    gl_FragColor = vec4(0, 0, 0, 1);
}
