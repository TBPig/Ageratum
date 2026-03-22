/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text;

import dev.anvilcraft.resource.ageratum.client.rendering.text.ttf.TtfGlyphAtlas;

public record GlyphDescriptor(
        TtfGlyphAtlas atlas,
        TtfGlyphAtlas.GlyphUV uv,
        int width,
        int height,
        int xOffset,
        int yOffset,
        int advance
) {
}