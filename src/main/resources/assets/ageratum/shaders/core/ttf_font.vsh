/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
#version 330 core

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

out vec4 v_Color;
out vec2 v_TexCoord;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    v_Color = Color;
    v_TexCoord = UV0;
}