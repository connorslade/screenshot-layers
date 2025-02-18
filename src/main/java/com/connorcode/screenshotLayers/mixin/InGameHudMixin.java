package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderMiscOverlays", at = @At("TAIL"))
    void onRenderMiscOverlaysTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!needsOverlayLayer()) return;

        context.draw();
        ScreenshotLayers.screenshotLayer("Overlays");
    }

    @Unique
    boolean needsOverlayLayer() {
        if (client.options.getPerspective().isFirstPerson()) {
            assert client.player != null;
            if (client.player.isUsingSpyglass()) return true;
            for (var slot : EquipmentSlot.values()) {
                var equippableComponent = client.player.getEquippedStack(slot).get(DataComponentTypes.EQUIPPABLE);
                if (equippableComponent != null && equippableComponent.slot() == slot && equippableComponent.cameraOverlay().isPresent())
                    return true;
            }

            return this.client.player.getFrozenTicks() > 0 || client.player.nauseaIntensity > 0.0;
        }

        return false;
    }
}
