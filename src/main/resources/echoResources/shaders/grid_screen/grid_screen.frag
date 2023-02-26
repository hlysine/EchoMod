#version 120

#ifdef GL_ES
precision mediump float;
#endif

uniform float foreground_time;
uniform float background_time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform float period = 2.0;
uniform float speed = 0.4;
uniform float duration = 2.0;
uniform float alpha = 1.0;
uniform float amplitude = 1.0;

uniform vec4 background_inner_color = vec4(0.59, 0.89, 1.0, 1.0);
uniform vec4 background_outer_color = vec4(0.36, 0.09, 0.77, 1.0);

uniform float grid_radius = 100.0;
uniform float grid_border = 50.0;
uniform float grid_margin = 10.0;
uniform vec4 grid_color1 = vec4(0.27, 0.18, 0.99, 1.0);
uniform vec4 grid_color2 = vec4(0.4, 0.36, 1.0, 0.3);

float sineWave(float offset, float p) {
    return (sin((background_time / p + offset) * 2.0 * 3.1415926) + 1.0) / 2.0;
}

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.865);
}

vec4 grid(vec2 tc) {
    vec4 returnColor;

    float halfSize = grid_radius + grid_border + grid_margin;
    float rnd = random(vec2(abs(floor((tc.x - resolution.x / 2.0) / (halfSize * 2.0))), abs(floor((tc.y - resolution.y / 2.0) / (halfSize * 2.0)))));

    vec2 gridOffset = vec2(abs(mod(tc.x - resolution.x / 2.0, halfSize * 2.0) - halfSize), abs(mod(tc.y - resolution.y / 2.0, halfSize * 2.0) - halfSize));
    vec4 gridColor = grid_color1 * rnd + grid_color2 * (1.0 - rnd);
    if (gridOffset.x < grid_radius && gridOffset.y < grid_radius) {
        returnColor = gridColor;
    } else if (gridOffset.x < grid_radius + grid_border && gridOffset.y < grid_radius + grid_border) {
        returnColor = vec4(gridColor.rgb, gridColor.a * max(0.0, 1.0 - length(vec2(max(0.0, gridOffset.x - grid_radius), max(0.0, gridOffset.y - grid_radius))) / grid_border));
    } else {
        returnColor = vec4(0.0, 0.0, 0.0, 0.0);
    }

    return returnColor;
}

vec4 background(vec2 tc) {
    float color_dist = background_time * min(resolution.x, resolution.y) / 2.0;
    float color_width = 300.0;
    float corner_dist = length(resolution) / 2.0;

    float dist = length(tc - resolution / 2.0);

    vec4 color;
    if (dist < color_dist) {
        color = background_inner_color;
    } else if (dist > color_dist + color_width) {
        color = background_outer_color;
    } else {
        color = background_outer_color * (dist - color_dist) / color_width + background_inner_color * (color_dist + color_width - dist) / color_width;
    }

    if (dist > color_dist) {
        return color;
    } else {
        return vec4(color.rgb, color.a * (dist - color_dist + color_width) / color_width);
    }
}

void main(void) {
    float t = mod(foreground_time, duration / speed) * speed;

    vec2 tc = gl_FragCoord.xy / resolution;
    vec2 tc_circular = vec2(gl_FragCoord.x - (resolution.x - resolution.y) / 2.0, gl_FragCoord.y) / vec2(resolution.y, resolution.y);
    vec2 wavePos = -0.7 + 1.4 * tc_circular;
    float waveOffset = length(wavePos);

    float offset = cos(min(3.1415926 * 2.0, pow(waveOffset / t, 2.0) * t * 10.0)) * t * amplitude * 2.0;
    vec2 uv = tc - wavePos * offset;

    vec4 background = background(gl_FragCoord.xy);
    vec4 foreground = grid(uv * resolution);

    gl_FragColor = vec4((background * (1 - foreground.a) + foreground * foreground.a).rgb, background.a * alpha);
}