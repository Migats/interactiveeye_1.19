#version 150

uniform sampler2D DiffuseSampler;

uniform vec4 ColorModulator;

uniform float Intensity;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main()
{
    //vec2 shaded_uv = vec2(texCoord.x+mod(floor(texCoord.y/0.01)*Intensity, 0.1), texCoord.y);
    vec4 color = texture(DiffuseSampler, texCoord) * vertexColor;

    // blit final output of compositor into displayed back buffer
    fragColor = color * ColorModulator;
}
