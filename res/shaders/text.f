varying vec2 texCoord;

uniform sampler2D tex;
uniform vec4 color;

void main()
{
    gl_FragColor = color * vec4(1.0, 1.0, 1.0, texture2D(tex, texCoord).r);
}