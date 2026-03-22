package dev.anvilcraft.resource.ageratum.client.rendering.text.ttf;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.anvilcraft.resource.ageratum.Ageratum;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static dev.anvilcraft.resource.ageratum.util.ResourceUtil.loadClientResource;
import static org.lwjgl.opengl.GL46.*;

public class TtfShader {

    public static final ResourceLocation VSH_LOCATION = Ageratum.location("shaders/core/ttf_font.vsh");
    public static final ResourceLocation FSH_LOCATION = Ageratum.location("shaders/core/ttf_font.fsh");

    private final int programId;

    private final int uProjMat;
    private final int uModelViewMat;
    private final int uSampler0;
    private final int uEdgeThreshold;

    public TtfShader() {
        int vert = compileShader(GL_VERTEX_SHADER,   loadClientResource(VSH_LOCATION));
        int frag = compileShader(GL_FRAGMENT_SHADER, loadClientResource(FSH_LOCATION));

        programId = glCreateProgram();
        glAttachShader(programId, vert);
        glAttachShader(programId, frag);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program link error:\n" + glGetProgramInfoLog(programId));
        }

        glDeleteShader(vert);
        glDeleteShader(frag);

        uProjMat       = glGetUniformLocation(programId, "ProjMat");
        uModelViewMat  = glGetUniformLocation(programId, "ModelViewMat");
        uSampler0      = glGetUniformLocation(programId, "Sampler0");
        uEdgeThreshold = glGetUniformLocation(programId, "EdgeThreshold");
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setGlyphSampler(TtfGlyphAtlas glyphSampler) {
        RenderSystem.activeTexture(GL_TEXTURE0);
        glyphSampler.bind();
        glUniform1i(uSampler0, 0);
    }

    public void setEdgeThreshold(float threshold) {
        glUniform1f(uEdgeThreshold, threshold);
    }

    public void setMatrices(Matrix4f projMat, Matrix4f modelViewMat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);

            projMat.get(buf);
            glUniformMatrix4fv(uProjMat, false, buf);

            modelViewMat.get(buf);
            glUniformMatrix4fv(uModelViewMat, false, buf);
        }
    }

    public void delete() {
        glDeleteProgram(programId);
    }

    private static int compileShader(int type, ByteBuffer source) {
        int id = glCreateShader(type);
        glShaderSource(id, bufferToString(source));
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(id);
            glDeleteShader(id);
            throw new RuntimeException("Shader compile error (" + type + "):\n" + log);
        }

        return id;
    }

    private static String bufferToString(ByteBuffer buf) {
        byte[] bytes = new byte[buf.remaining()];
        buf.duplicate().get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}