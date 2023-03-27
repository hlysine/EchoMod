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

uniform vec4 gradient_color1 = vec4(0.31, 0.27, 0.82, 0.5);
uniform vec4 gradient_color2 = vec4(0.27, 0.21, 0.78, 0.5);

uniform float grid_radius = 1.0;
uniform float grid_border = 3.0;
uniform float grid_margin = 1.0;
uniform vec4 grid_color = vec4(0.17, 0.87, 1.0, 0.75);

uniform float contrast = 2;

uniform bool grayscale = false;

uniform float radius = 10000.0;
uniform float border_width = 50.0;
uniform vec2 center = vec2(0, 0);

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

vec4 gradient(vec2 tc, vec2 texSize) {
    float dist1 = length(tc - vec2(0.0, 0.0));
    float dist2 = length(tc - texSize);

    vec4 color1 = gradient_color1 * sineWave(0, period) + gradient_color2 * (1 - sineWave(0, period));
    vec4 color2 = gradient_color1 * sineWave(0.25, period) + gradient_color2 * (1 - sineWave(0.25, period));

    return color1 * (dist2 / (dist1 + dist2)) + color2 * (dist1 / (dist1 + dist2));
}

void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec2 texSize = vec2(res_x, res_y);
    vec2 tc = v_texCoords * texSize;

    vec4 finalColor;
    if (texColor.a < 0.01) {
        finalColor = v_color * texColor;
    } else {
        vec4 gradientColor = gradient(tc, texSize);
        vec4 gridColor = grid(tc, texSize);

        vec4 tint = gradientColor * (1 - gridColor.a) + gridColor * gridColor.a;
        float contrastValue = length(texColor.rgb);
        contrastValue = (contrastValue - 0.5) * contrast + 0.5;
        finalColor = v_color * vec4((tint * (1.0 - tint.a * 0.25 + tint.a * contrastValue)).rgb, texColor.a);
    }

    if (grayscale) {
        float gray = dot(finalColor.rgb, vec3(0.299, 0.587, 0.114));
        finalColor = vec4(gray, gray, gray, finalColor.a);
    }

    float dist_from_center = length(tc - center);
    if (dist_from_center > radius) {
        finalColor = finalColor * vec4(1.0, 1.0, 1.0, 0.0);
    } else if (dist_from_center > radius - border_width) {
        finalColor = finalColor * vec4(1.0, 1.0, 1.0, 1.0 - (dist_from_center - (radius - border_width)) / border_width);
    }

    gl_FragColor = finalColor;
}