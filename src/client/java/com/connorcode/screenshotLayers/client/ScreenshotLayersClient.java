package com.connorcode.screenshotLayers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class ScreenshotLayersClient implements ClientModInitializer {
    public static boolean takingScreenshot = false;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("screenshot").executes(ctx -> {
                ctx.getSource().sendFeedback(Text.literal("Taking screenshot..."));
                takingScreenshot = true;
                return 1;
            }));
        });
    }
}
