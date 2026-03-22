/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text;

import javax.annotation.Nullable;

public interface IFontLoader {

    void checkAndLoadChar(char ch);

    void checkAndLoadChars(String chars);

    void destroy();

    @Nullable
    GlyphDescriptor getGlyph(char ch);

}