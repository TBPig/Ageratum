/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text.ttf;

import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class TtfGlyphAtlas {
    private static final int SIZE = 512;

    @Getter
    private final int atlasId;
    @Getter
    private final int texture;

    private int currentX = 0;
    private int currentY = 0;
    private int currentRowHeight = 0;

    public TtfGlyphAtlas(int atlasId) {
        this.atlasId = atlasId;
        this.texture = createGlyphAtlas(SIZE);
        System.out.println("[TtfGlyphAtlas] Created 512x512 texture: " + texture);
    }

    /**
     * Try to append a glyph to atlas
     * <p>
     * Return null if glyph atlas is full
     */
    public GlyphUV appendGlyph(TtfGlyph glyph) {
        if (glyph.glyphData() == null) return null;

        if (currentX + glyph.width() >= SIZE) {
            currentX = 0;
            currentY += currentRowHeight;
            currentRowHeight = 0;
        }

        // Return null if glyph atlas is full
        if (currentY + glyph.height() >= SIZE) {
            System.out.printf("Rejecting glyph: " + glyph, currentX, currentY);
            return null;
        }
        glBindTexture(GL_TEXTURE_2D, this.texture);
        System.out.printf("currentXY: %d %d | %d\n", currentX, currentY, currentY + glyph.height());

        glPixelStorei(GL_UNPACK_ROW_LENGTH, glyph.width());
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexSubImage2D(
            GL_TEXTURE_2D,
            0,
            currentX,
            currentY,
            glyph.width(),
            glyph.height(),
            GL_LUMINANCE,
            GL_UNSIGNED_BYTE,
            glyph.glyphData()
        );

        int spacePixel = 1;

        GlyphUV uv = new GlyphUV(
            (float) (currentX + spacePixel) / SIZE,
            (float) (currentY + spacePixel) / SIZE,
            (float) (currentX + glyph.width() - 2 * spacePixel) / SIZE,
            (float) (currentY + glyph.height() - 2 * spacePixel) / SIZE
        );

        currentX += glyph.width();
        currentRowHeight = Math.max(currentRowHeight, glyph.height());

        return uv;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        glDeleteTextures(texture);
    }

    public record GlyphUV(float u0, float v0, float u1, float v1) {
    }

    public static int createGlyphAtlas(int size) {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_R8, size, size, 0, GL_RED, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        glBindTexture(GL_TEXTURE_2D, 0);
        return texture;
    }
}