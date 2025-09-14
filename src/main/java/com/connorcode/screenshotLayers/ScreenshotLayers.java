package com.connorcode.screenshotLayers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ScreenshotLayers implements ClientModInitializer {
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static KeyBinding captureKeybinding;
    public static ScreenshotBuilder builder;

    @Override
    public void onInitializeClient() {
        captureKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.screenshot-layers.capture", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F2, "category.screenshot-layers"));
    }

    public static void screenshotLayer(String name, int n) {
        if (builder == null || builder.stack.get(n) != null) return;

        var fb = client.getFramebuffer();
        var pixels = fb.textureWidth * fb.textureHeight;

        var texture = fb.getColorAttachment();
        assert texture != null;
        var pixelSize = texture.getFormat().pixelSize();

        var buffer = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", 9, pixels * pixelSize);
        var commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        builder.markLayer(n);

        commandEncoder.copyTextureToBuffer(texture, buffer, 0, () -> new Thread(() -> {
            var image = new NativeImage(fb.textureWidth, fb.textureHeight, false);
            var view = commandEncoder.mapBuffer(buffer, true, false);

            for (int y = 0; y < fb.textureHeight; y++) {
                for (int x = 0; x < fb.textureWidth; x++) {
                    var pixel = view.data().getInt((x + y * fb.textureWidth) * pixelSize);
                    image.setColor(x, fb.textureHeight - y - 1, pixel | -16777216);
                }
            }

            builder.pushLayer(new ScreenshotBuilder.ScreenshotLayer(image, name), n);
        }).start(), 0);
    }
}
