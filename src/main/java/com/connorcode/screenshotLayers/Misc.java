package com.connorcode.screenshotLayers;

import com.connorcode.screenshotLayers.mixin.GameRendererAccessor;
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
        var gameRenderer = ((GameRendererAccessor) client.gameRenderer);
        var guiRenderer = gameRenderer.getGuiRenderer();

        guiRenderer.render(gameRenderer.getFogRenderer().getFogBuffer(FogRenderer.FogType.NONE));
        guiRenderer.incrementFrame();
    }
}
