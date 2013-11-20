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

uniform vec3 un_PickingColor; // the one that matters

void main(void)
{
	// Write out the picking color unchanged. This is a
	// hacky means of outputting (an encoding of) an
	// integer object identifier at each pixel for use
	// with selecting objects in the scene with the mouse.
	gl_FragColor = vec4(un_PickingColor, 1.0);
}

