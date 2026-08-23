package com.snapvisual.aim.mixin;

import com.snapvisual.aim.module.ModuleManager;
import com.snapvisual.aim.module.modules.FOVModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        FOVModule fov = (FOVModule) ModuleManager.getModule("FOV Circle");
        if (fov != null && fov.isEnabled() && fov.isVisible()) {
            int cx = context.getScaledWindowWidth() / 2;
            int cy = context.getScaledWindowHeight() / 2;
            int r = (int) fov.getRadius();
            int color = 0xFFDC3C3C;
            for (int i = 0; i < 360; i += 3) {
                double rad = Math.toRadians(i);
                int x = cx + (int)(Math.cos(rad) * r);
                int y = cy + (int)(Math.sin(rad) * r);
                context.fill(x, y, x + 1, y + 1, color);
            }
            context.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFFFFFFFF);
        }
    }
}
