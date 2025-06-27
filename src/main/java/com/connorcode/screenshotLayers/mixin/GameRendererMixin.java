package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.connorcode.screenshotLayers.Misc.asInt;
import static com.connorcode.screenshotLayers.ScreenshotLayers.builder;
import static com.connorcode.screenshotLayers.ScreenshotLayers.client;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    void onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (ScreenshotLayers.builder == null) return;
        ScreenshotLayers.builder.wasHudHidden = client.options.hudHidden;
        client.options.hudHidden = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FZLorg/joml/Matrix4f;)V"))
    void onRenderWorldBeforeHand(CallbackInfo ci) {
        if (builder != null)
            ScreenshotLayers.screenshotLayer("World", 0);
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        if (builder != null && builder.layers.hand)
            ScreenshotLayers.screenshotLayer("Player Hand", 1);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", shift = At.Shift.AFTER))
    void onFirstDrawContextDraw(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (builder != null)
            ScreenshotLayers.screenshotLayer("In Game HUD", 2 + asInt(builder.layers.hand) + asInt(builder.layers.overlay));
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderInGameHud(CallbackInfo ci) {
        if (builder != null && builder.layers.screen)
            ScreenshotLayers.screenshotLayer("Screen", 3 + asInt(builder.layers.hand) + asInt(builder.layers.overlay));
    }
}
