package dev.anvilcraft.resource.ageratum.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ResourceUtil {

    public static ByteBuffer loadClientResource(ResourceLocation file) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Resource resource = resourceManager.getResource(file).orElseThrow();
        try (InputStream inputStream = resource.open()) {
            byte[] bytes = inputStream.readAllBytes();
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            return byteBuffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
