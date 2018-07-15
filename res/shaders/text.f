#version 150 core
in vec2 texCoord;
out vec4 outColor;

uniform sampler2D tex;
uniform vec4 color;

void main()
{
    outColor = color * vec4(1.0, 1.0, 1.0, texture(tex, texCoord).r);
}