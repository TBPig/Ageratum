/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text;

public interface IFontLoader {

    void checkAndLoadChar(char ch);

    void checkAndLoadChars(String chars);

    void destroy();

    GlyphDescriptor getGlyph(char ch);

}