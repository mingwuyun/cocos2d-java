// 冰花效果
varying vec2 v_texCoords;
uniform sampler2D u_texture;

float rand(vec2 co)
{
    return fract(sin(dot(co.xy ,vec2(100,100))) +
                 cos(dot(co.xy ,vec2(50,50))) *5.0);
}

void main()
{
    vec2 rnd = vec2(0.0);
    rnd = vec2(rand(v_texCoords),rand(v_texCoords));
    gl_FragColor = texture2D(u_texture, v_texCoords+rnd*0.02);
}