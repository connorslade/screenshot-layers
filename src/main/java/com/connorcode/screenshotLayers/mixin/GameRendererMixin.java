package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

import static com.connorcode.screenshotLayers.Misc.screenshotFilename;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private boolean renderHand;
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    void onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (ScreenshotLayers.builder == null) return;
        ScreenshotLayers.builder.wasHudHidden = client.options.hudHidden;
        client.options.hudHidden = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    void onRenderWorldBeforeHand(CallbackInfo ci) {
        if (this.renderHand) ScreenshotLayers.screenshotLayer("World");
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer("Player Hand");
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(I)V", ordinal = 1))
    void onFirstDrawContextDraw(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer("In Game HUD");
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderInGameHud(CallbackInfo ci) {
        var inGameHud = (InGameHudAccessor) client.inGameHud;
        var showAutosave = client.options.getShowAutosaveIndicator().getValue() && (inGameHud.getAutosaveIndicatorAlpha() > 0.0 || inGameHud.getLastAutosaveIndicatorAlpha() > 0.0);
        if (client.currentScreen != null || showAutosave)
            ScreenshotLayers.screenshotLayer("Screen");


        var builder = ScreenshotLayers.builder;
        if (builder != null && !builder.isEmpty()) {
            client.options.hudHidden = builder.wasHudHidden;

            new Thread(() -> {
                var chat = client.inGameHud.getChatHud();
                var file = screenshotFilename();
                try {
                    builder.saveTiff(file);
                    if (client.world != null)
                        chat.addMessage(Text.literal(String.format("Saved screenshot as %s", file.getName())));
                } catch (IOException e) {
                    if (client.world != null)
                        chat.addMessage(Text.literal(String.format("Failed to take screenshot: %s", e.getMessage())));
                }
            }).start();
            ScreenshotLayers.builder = null;
        }
    }
}
