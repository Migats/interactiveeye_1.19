#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

uniform float Intensity;

in vec2 texCoord0;

out vec4 fragColor;

void main()
{
    vec2 shaded_uv = vec2(texCoord0.x+mod(floor(texCoord0.y/0.01)*Intensity, 0.1), texCoord0.y);
    vec4 color = texture(Sampler0,shaded_uv);
    if (color.a == 0.0) {
        color.a = 0.0;
    }
    fragColor = color * ColorModulator * vec4(1.0, 0.0, 1.0, 1.0);

}
