#version 120

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 resolution;

uniform float time;
uniform float period = 5.0;

uniform float alpha = 1.0;

uniform vec4 gradient_color1 = vec4(0.21, 0.67, 0.82, 1);
uniform vec4 gradient_color2 = vec4(0.17, 0.87, 1.0, 1);

uniform float grid_radius = 150.0;
uniform float grid_border = 150.0;
uniform float grid_margin = 30.0;
uniform vec4 grid_color = vec4(0.27, 0.21, 0.78, 0.75);

float sineWave(float offset, float p) {
    return (sin((time / p + offset) * 2.0 * 3.1415926) + 1.0) / 2.0;
}

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.865);
}

vec4 grid(vec2 tc, vec2 texSize) {
    vec4 returnColor;

    float halfSize = grid_radius + grid_border + grid_margin;
    vec2 gridOffset = vec2(abs(mod(tc.x - texSize.x / 2.0, halfSize * 2.0) - halfSize), abs(mod(tc.y - texSize.y / 2.0, halfSize * 2.0) - halfSize));
    if (gridOffset.x < grid_radius && gridOffset.y < grid_radius) {
        returnColor = grid_color;
    } else if (gridOffset.x < grid_radius + grid_border && gridOffset.y < grid_radius + grid_border) {
        returnColor = vec4(grid_color.rgb, grid_color.a * max(0.0, 1.0 - length(vec2(max(0.0, gridOffset.x - grid_radius), max(0.0, gridOffset.y - grid_radius))) / grid_border));
    } else {
        returnColor = vec4(0.0, 0.0, 0.0, 0.0);
    }

    float rnd = random(vec2(abs(floor((tc.x - texSize.x / 2.0) / (halfSize * 2.0))), abs(floor((tc.y - texSize.y / 2.0) / (halfSize * 2.0)))));
    return vec4(returnColor.rgb, returnColor.a * pow(0.3 + 0.7 * sineWave(rnd, period * (0.5 + rnd)), 1.5));
}

vec4 gradient(vec2 tc, vec2 texSize) {
    float dist1 = length(tc - vec2(0.0, 0.0));
    float dist2 = length(tc - texSize);

    vec4 color1 = gradient_color1 * sineWave(0.0, period) + gradient_color2 * (1.0 - sineWave(0.0, period));
    vec4 color2 = gradient_color1 * sineWave(0.25, period) + gradient_color2 * (1.0 - sineWave(0.25, period));

    return color1 * (dist2 / (dist1 + dist2)) + color2 * (dist1 / (dist1 + dist2));
}


void main(void) {
    vec2 tc = gl_FragCoord.xy;

    vec4 gradientColor = gradient(tc, resolution);
    vec4 gridColor = grid(tc, resolution);

    gl_FragColor = vec4((gradientColor * (1.0 - gridColor.a) + gridColor * gridColor.a).rgb, alpha);
}