#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform float un_FixedDepth;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;
attribute vec2 in_TexCoord;

varying vec2 texCoord;

void main() {
	// A simple debugging tool: take the NDC position from the texture coordinates,
	// ignoring the actual vertex position entirely.  This gives you a map of the
	// mesh in texture space, in which each triangle is drawn over the part of
	// the texture that it will get its values from.
	texCoord = in_TexCoord;
	
	// Scale so that the range [0,1]x[0,1] of texture space exactly fills up the
	// window, which is [-1,1]x[-1,1] in NDC.
	gl_Position.xy = 2 * in_TexCoord.xy - 1;
	
	// The z position comes from a uniform so we can statically control what
	// comes out on top.  TexCoordMaterial uses this to put the wireframe on top.
	gl_Position.z = un_FixedDepth;
	gl_Position.w = 1;
}