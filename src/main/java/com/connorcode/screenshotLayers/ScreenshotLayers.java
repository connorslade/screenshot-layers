package com.connorcode.screenshotLayers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.util.ScreenshotRecorder.takeScreenshot;

public class ScreenshotLayers implements ClientModInitializer {
    public static KeyBinding captureKeybinding;
    public static ScreenshotBuilder builder;

    public static void screenshotLayer(String name) {
        if (builder == null) return;

        var client = MinecraftClient.getInstance();
        var layer = takeScreenshot(client.getFramebuffer());
        ScreenshotLayers.builder.pushLayer(new ScreenshotBuilder.ScreenshotLayer(layer, name));
    }

    @Override
    public void onInitializeClient() {
        captureKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.screenshot-layers.capture",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F2,
                "category.screenshot-layers"
        ));
    }
}
