
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

// Transform between the object's local frame and
// the "yellow" light-aligned frame in Figures 5/6/7
uniform mat4 un_FrameToObj;
uniform mat4 un_ObjToFrame;

// TODO: (Shaders 1 Problem 2) Declare any additional uniform variables here
uniform float R;
uniform float h;
uniform float Phi;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

// TODO: (Shaders 1 Problem 2) Declare any varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main()
{
	// TODO: (Shaders 1 Problem 2) Implement the vertex shader for the
	// flower shader here
	
	// First transform object coordinates to frame coordinates
	vec3 frameVertex = (un_ObjToFrame * vec4(in_Vertex, 1)).xyz;
	vec3 frameNormal = (un_ObjToFrame * vec4(in_Normal, 1)).xyz;
	
	// Compute frame coordinates when bent
	float r = R - frameVertex.x;
	float subPhi = frameVertex.y / h * Phi;
	float x = R - r * cos(subPhi);
	float y = r * sin(subPhi);
	vec3 bentframeVertex = vec3(x, y, frameVertex.z);
	mat3 rotation = mat3(cos(-Phi),sin(-Phi),0, -sin(-Phi),cos(-Phi),0, 0,0,1);
	vec3 bentframeNormal = rotation * frameNormal;
	
	// Map back to object space
	vec3 bentVertex = (un_FrameToObj * vec4(bentframeVertex, 1)).xyz;
	vec3 bentNormal = (un_FrameToObj * vec4(bentframeNormal, 1)).xyz;
	
    gl_Position = un_Projection * un_ModelView * vec4(bentVertex, 1);
    
    ex_Normal = normalize(un_NormalMatrix * bentNormal);
    ex_EyeSpacePosition = un_ModelView * vec4(bentVertex, 1);
}

