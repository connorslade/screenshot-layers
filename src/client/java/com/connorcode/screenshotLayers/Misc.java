package com.connorcode.screenshotLayers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.File;

public class Misc {
    public static String screenshotFilename() {
        var directory = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
        var time = Util.getFormattedCurrentTime();

        for (int i = 1; ; i++) {
            File file = new File(directory, time + (i == 1 ? "" : "_" + i) + ".tif");
            if (!file.exists()) return file.getPath();
        }
    }
}
