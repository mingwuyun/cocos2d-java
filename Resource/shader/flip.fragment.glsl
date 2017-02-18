// 反色
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main()
{
	vec4 col = texture2D( u_texture, v_texCoords);
	col.g=1.0-col.g; //
	col.r=1.0-col.r; //
	col.b=1.0-col.b; //
	gl_FragColor = col;
    // lowp vec4 col = texture2D(u_texture, v_texCoords);
     //col.g = 1.0 - col.g;
     //gl_FragColor = col;
}