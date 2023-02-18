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

// Swirl effect parameters
uniform float radius = 200.0;
uniform float angle = 0.8;
uniform vec2 center = vec2(400.0, 300.0);

uniform vec2 focus = vec2(600, 300.0);
uniform float focus_radius = 100.0;
uniform float focus_radius_2 = 500.0;

vec4 PostFX(sampler2D tex, vec2 uv)
{
    vec2 texSize = vec2(res_x, res_y);
    vec2 orig_tc = uv * texSize;
    vec2 tc = orig_tc;
    tc -= center;
    float dist = length(tc);
    if (dist < radius)
    {
        float percent = (radius - dist) / radius;
        float theta = percent * percent * angle * 8.0;
        float s = sin(theta);
        float c = cos(theta);
        tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
    }
    tc += center;
    float clear_dist = length(orig_tc - focus);
    if (clear_dist < focus_radius) {
        tc = orig_tc;
    } else if (clear_dist < focus_radius_2) {
        float progress = (clear_dist - focus_radius) / (focus_radius_2 - focus_radius);
        tc = orig_tc * (1.0 - progress) + tc * progress;
    }
    vec3 color = texture2D(u_texture, tc / texSize).rgb;
    return vec4(color, 1.0);
}

void main()
{
    gl_FragColor = v_color * PostFX(u_texture, v_texCoords);
}