attribute vec2 position;
attribute vec2 textureCoordinate;
varying vec2 texCoord;
uniform mat4 matrix;
uniform vec2 frameStart;
uniform vec2 frameSize;
void main()
{
    gl_Position = matrix * vec4(position, 0.0, 1.0);
    texCoord = frameStart + vec2(textureCoordinate.x, 1.0 - textureCoordinate.y) * frameSize;
}