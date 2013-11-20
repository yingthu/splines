#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_DiffuseColor;

uniform sampler2D un_Texture;

varying vec2 texCoord;

void main() {
	// Compute the fragment color as the texture color, scaled by the diffuse color
	vec4 texColor = texture2D(un_Texture, texCoord);
	gl_FragColor.rgb = texColor.rgb * un_DiffuseColor;
	// Alpha value comes straight from the texture
	gl_FragColor.a = texColor.a;
}