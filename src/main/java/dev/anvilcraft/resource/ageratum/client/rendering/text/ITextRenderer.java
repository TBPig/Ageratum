/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.resource.ageratum.client.rendering.text.ttf.TtfFontLoader;

public interface ITextRenderer {

    void addText(String text, float x, float y, float z, float scale, PoseStack poseStack, int argb, TtfFontLoader fontLoader);

    void draw();

    void reset();

    void close();

    float getHeight(float scale, TtfFontLoader fontLoader);

    float getWidth(String text, float scale, TtfFontLoader fontLoader);

    default void setScissor(int x, int y, int width, int height) {
    }

    default void clearScissor() {
    }

}