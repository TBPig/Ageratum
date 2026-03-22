/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text.ttf;

import dev.anvilcraft.resource.ageratum.client.rendering.text.GlyphDescriptor;
import dev.anvilcraft.resource.ageratum.client.rendering.text.IFontLoader;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.stb.STBTruetype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class TtfFontLoader implements IFontLoader {

    public final TtfFontFile fontFile;

    private final Map<Character, GlyphDescriptor> glyphMap = new HashMap<>();
    private final List<TtfGlyphAtlas> atlases = new ArrayList<>();

    private TtfGlyphAtlas currentAtlas;
    private int atlasId = 0;

    public TtfFontLoader(ResourceLocation ttfFile) {
        this.fontFile = new TtfFontFile(ttfFile, 64, 6);
    }

    @Override
    public void checkAndLoadChar(char ch) {
        if (glyphMap.containsKey(ch)) return;

        TtfGlyph glyph = fontFile.generateGlyph(ch);
        if (glyph.glyphData() == null) return;

        if (currentAtlas == null) {
            createNewAtlas();
        }

        TtfGlyphAtlas.GlyphUV uv = currentAtlas.appendGlyph(glyph);

        if (uv == null) {
            createNewAtlas();
            uv = currentAtlas.appendGlyph(glyph);
        }

        if (uv != null) {
            glyphMap.put(ch, new GlyphDescriptor(
                    currentAtlas, uv,
                    glyph.width(), glyph.height(),
                    glyph.xOffset(), glyph.yOffset(),
                    glyph.advance()
            ));
        }

        STBTruetype.stbtt_FreeSDF(glyph.glyphData());
    }

    private void createNewAtlas() {
        currentAtlas = new TtfGlyphAtlas(atlasId);
        atlases.add(currentAtlas);
        atlasId++;
    }

    @Override
    public void checkAndLoadChars(String chars) {
        for (final var ch : chars.toCharArray()) {
            checkAndLoadChar(ch);
        }
    }


    @Override
    public void destroy() {
        fontFile.destroy();
        for (TtfGlyphAtlas atlas : atlases) {
            atlas.destroy();
        }
        atlases.clear();
        glyphMap.clear();
    }

    @Override
    public @Nullable GlyphDescriptor getGlyph(char ch) {
        return glyphMap.get(ch);
    }
}