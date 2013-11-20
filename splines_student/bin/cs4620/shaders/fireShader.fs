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

uniform sampler2D un_NoiseTexture;
uniform sampler2D un_FireTexture;
uniform float un_Time;

uniform vec3 un_ScrollSpeeds;

// TODO (Shaders 2 P3): Declare varying variables here
varying vec2 ex_TexCoord;

void main() {
	// TODO (Shaders 2 P3): Implement the fire fragment shader here
	vec2 texCoordOne = vec2(ex_TexCoord.s, 
        mod((ex_TexCoord.t + un_Time * un_ScrollSpeeds.x), 1.0));
    vec4 sampleOne = texture2D(un_NoiseTexture, texCoordOne);
        
    vec2 texCoordTwo = vec2(ex_TexCoord.s, 
        mod((ex_TexCoord.t + un_Time * un_ScrollSpeeds.y), 1.0));
    vec4 sampleTwo = texture2D(un_NoiseTexture, texCoordTwo);
        
    vec2 texCoordThree = vec2(ex_TexCoord.s, 
        mod((ex_TexCoord.t + un_Time * un_ScrollSpeeds.z), 1.0));
    vec4 sampleThree = texture2D(un_NoiseTexture, texCoordThree);
        
    /* Average all the values and sample from fireTexture*/
    vec4 avgSample = ((sampleOne + sampleTwo + sampleThree)) / 3.0;
    gl_FragColor = texture2D(un_FireTexture, avgSample.st);
}