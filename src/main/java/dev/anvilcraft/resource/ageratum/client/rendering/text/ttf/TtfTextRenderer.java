/*
 * SPDX-License-Identifier: MIT
 *
 * This file is derived from the Epsilon-Rewrite project
 * Original source: https://github.com/KonekokoHouse/Epsilon-Rewrite
 */
package dev.anvilcraft.resource.ageratum.client.rendering.text.ttf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.anvilcraft.resource.ageratum.client.rendering.text.GlyphDescriptor;
import dev.anvilcraft.resource.ageratum.client.rendering.text.ITextRenderer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class TtfTextRenderer implements ITextRenderer {
    private static final float DEFAULT_SCALE = 0.27f;
    private static final float SPACING = 1f;
    private final int bufferSize;

    private final Map<TtfGlyphAtlas, DrawContext> contextMap = new IdentityHashMap<>();
    private final Map<RenderStatePack, BufferBuilder> batchMap = new HashMap<>();
    private final TtfShader shader = new TtfShader();
    private @Nullable ScissorState scissorState;
    private boolean isEmpty = true;

    public TtfTextRenderer(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public TtfTextRenderer() {
        this(16 * 1024);
    }

    public DrawContext getOrCreateContext(TtfGlyphAtlas atlas, TtfFontLoader fontLoader) {
        DrawContext drawContext = contextMap.get(atlas);
        if (drawContext == null) {
            System.out.println("ALLOCATING!!!");
            drawContext = new DrawContext(
                new ByteBufferBuilder(bufferSize),
                new VertexBuffer(VertexBuffer.Usage.STATIC),
                fontLoader,
                atlas
            );
            contextMap.put(atlas, drawContext);
        }
        return drawContext;
    }

    public BufferBuilder getOrBeginBatch(GlyphDescriptor descriptor, @Nullable ScissorState scissorState, TtfFontLoader fontLoader) {
        TtfGlyphAtlas atlas = descriptor.atlas();
        DrawContext context = getOrCreateContext(atlas, fontLoader);
        RenderStatePack renderStatePack = new RenderStatePack(context, scissorState);
        BufferBuilder builder = batchMap.get(renderStatePack);
        if (builder == null) {
            builder = new BufferBuilder(context.byteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            batchMap.put(renderStatePack, builder);
        }
        return builder;
    }

    @Override
    public void addText(
        String text,
        float x,
        float y,
        float z,
        float scale,
        PoseStack poseStack,
        int argb,
        @Nullable TtfFontLoader fontLoader
    ) {
        final var finalScale = scale * DEFAULT_SCALE;
        if (fontLoader == null) return;
        fontLoader.checkAndLoadChars(text);

        float xOffset = 0f;
        float yOffset = 0f;
        PoseStack.Pose pose = poseStack.last();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                xOffset += 3.0f * scale;
                continue;
            }
            if (ch == '\n') {
                xOffset = 0f;
                yOffset += fontLoader.fontFile.fontHeight * finalScale;
                continue;
            }

            GlyphDescriptor glyph = fontLoader.getGlyph(ch);
            if (glyph == null) continue;

            float baselineY = yOffset + y + (fontLoader.fontFile.pixelAscent * finalScale);
            float x1 = x + xOffset;
            float x2 = x1 + glyph.width() * finalScale;
            float y1 = baselineY + glyph.yOffset() * finalScale;
            float y2 = y1 + glyph.height() * finalScale;

            VertexConsumer vertexConsumer = getOrBeginBatch(glyph, this.scissorState, fontLoader);

            vertexConsumer.addVertex(pose, x1, y1, z)
                .setUv(glyph.uv().u0(), glyph.uv().v0())
                .setColor(argb);

            vertexConsumer.addVertex(pose, x1, y2, z)
                .setUv(glyph.uv().u0(), glyph.uv().v1())
                .setColor(argb);

            vertexConsumer.addVertex(pose, x2, y2, z)
                .setUv(glyph.uv().u1(), glyph.uv().v1())
                .setColor(argb);

            vertexConsumer.addVertex(pose, x2, y1, z)
                .setUv(glyph.uv().u1(), glyph.uv().v0())
                .setColor(argb);

            xOffset += glyph.advance() * finalScale + SPACING * scale;

        }
        isEmpty = false;
    }

    @Override
    public void draw() {
        if (isEmpty) return;
        shader.bind();
        shader.setMatrices(RenderSystem.getProjectionMatrix(), RenderSystem.getModelViewMatrix());
        shader.setEdgeThreshold(0.5f);
        for (Map.Entry<RenderStatePack, BufferBuilder> entry : batchMap.entrySet()) {
            RenderStatePack key = entry.getKey();
            BufferBuilder value = entry.getValue();
            shader.setGlyphSampler(key.drawContext.atlas);
            MeshData meshData = value.build();
            if (meshData == null) continue;
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            key.drawContext.vertexBuffer.bind();
            key.drawContext.vertexBuffer.upload(meshData);
            key.drawContext.vertexBuffer.draw();
            VertexBuffer.unbind();
        }
        shader.unbind();
        batchMap.clear();
    }

    @Override
    public void reset() {
        batchMap.clear();
    }

    @Override
    public void close() {
        isEmpty = true;
    }

    @Override
    public float getHeight(float scale, TtfFontLoader fontLoader) {
        return fontLoader.fontFile.pixelAscent * DEFAULT_SCALE * scale;
    }

    @Override
    public float getWidth(String text, float scale, TtfFontLoader fontLoader) {
        fontLoader.checkAndLoadChars(text);
        final var finalScale = scale * DEFAULT_SCALE;
        float maxLine = 0.0f;
        float currentLine = 0.0f;

        for (char ch : text.toCharArray()) {
            if (ch == ' ') {
                currentLine += 3.0f * scale;
            } else if (ch == '\n') {
                maxLine = Math.max(maxLine, currentLine);
                currentLine = 0.0f;
            } else {
                GlyphDescriptor glyph = fontLoader.getGlyph(ch);
                if (glyph != null) {
                    currentLine += glyph.advance() * finalScale + SPACING * scale;
                }
            }
        }
        return Math.max(maxLine, currentLine);
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        this.scissorState = new ScissorState(x, y, width, height);
    }

    @Override
    public void clearScissor() {
        this.scissorState = null;
    }

    public record ScissorState(
        int x,
        int y,
        int width,
        int height
    ) {
    }

    public record RenderStatePack(
        DrawContext drawContext,
        @Nullable ScissorState scissorState
    ) {

        @Override
        public int hashCode() {
            return System.identityHashCode(drawContext) + (scissorState == null ? 0 : scissorState.hashCode() * 42);
        }
    }

    public record DrawContext(
        ByteBufferBuilder byteBuffer,
        VertexBuffer vertexBuffer,
        TtfFontLoader fontLoader,
        TtfGlyphAtlas atlas
    ) {
        void dispose() {
            byteBuffer.close();
            vertexBuffer.close();
        }
    }
}