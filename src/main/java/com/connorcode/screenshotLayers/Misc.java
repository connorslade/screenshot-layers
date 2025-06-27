package com.connorcode.screenshotLayers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.util.Util;

import java.io.File;

import static com.connorcode.screenshotLayers.ScreenshotLayers.client;

public class Misc {
    public static File screenshotFilename() {
        var directory = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
        var time = Util.getFormattedCurrentTime();

        for (int i = 1; ; i++) {
            File file = new File(directory, time + (i == 1 ? "" : "_" + i) + ".tif");
            if (!file.exists()) return file;
        }
    }

    public static int asInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static void renderGui() {
        var guiRenderer = client.gameRenderer.guiRenderer;
        var emptyBuffer = client.gameRenderer.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE);

        guiRenderer.render(emptyBuffer);
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(client.getFramebuffer().getDepthAttachment(), 1.0);
    }
}
