#version 120

#ifdef GL_ES
precision mediump float;
#endif

// varying input variables from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

// a special uniform for textures
uniform sampler2D u_texture;

// size of current render target
uniform float res_x;
uniform float res_y;

uniform float time = 0.0;
uniform float period = 5.0;

uniform float grid_radius = 1.0;
uniform float grid_border = 2.0;
uniform float grid_margin = 0.5;
uniform vec4 grid_color = vec4(1.0, 1.0, 1.0, 1.0);

float sineWave(float offset, float period) {
    return (sin((time / period + offset) * 2.0 * 3.1415926) + 1.0) / 2.0;
}

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.865);
}

vec4 grid(vec2 tc, vec2 texSize) {
    vec4 returnColor;

    float halfSize = grid_radius + grid_border + grid_margin;
    vec2 gridOffset = vec2(abs(mod(tc.x, halfSize * 2.0) - halfSize), abs(mod(tc.y, halfSize * 2.0) - halfSize));
    if (gridOffset.x < grid_radius && gridOffset.y < grid_radius) {
        returnColor = grid_color;
    } else if (gridOffset.x < grid_radius + grid_border && gridOffset.y < grid_radius + grid_border) {
        returnColor = vec4(grid_color.rgb, grid_color.a * max(0, 1 - length(vec2(max(0, gridOffset.x - grid_radius), max(0, gridOffset.y - grid_radius))) / grid_border));
    } else {
        returnColor = vec4(0.0, 0.0, 0.0, 0.0);
    }

    float rnd = random(vec2(floor(tc.x / (halfSize * 2.0)), floor(tc.y / (halfSize * 2.0))));
    return vec4(returnColor.rgb, returnColor.a * pow(sineWave(rnd, period * (0.5 + rnd)), 1.5));
}

void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec4 finalColor;
    if (texColor.a < 0.01) {
        finalColor = v_color * texColor;
    } else {
        vec2 texSize = vec2(res_x, res_y);
        vec2 tc = v_texCoords * texSize;

        if (v_texCoords.y > 0.5) {
            finalColor = v_color * texColor;
        } else {
            vec4 gridColor = grid(tc, texSize);
            gridColor.a = gridColor.a * (1.0 - v_texCoords.y * 2.0);

            finalColor = vec4((texColor * (1 - gridColor.a) + gridColor * gridColor.a).rgb, texColor.a) * v_color;
        }
    }

    gl_FragColor = finalColor;
}