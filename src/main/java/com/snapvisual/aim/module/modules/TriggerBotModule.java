package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBotModule extends Module {
    private double range = 4.5;
    private int cooldown = 12;
    private int ticks = 0;
    private boolean critOnly = true;

    public TriggerBotModule() {
        super("Триггер бот", "Авто-удар в момент крита при падении", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;
        if (ticks > 0) { ticks--; return; }

        if (critOnly && !isFalling()) return;

        HitResult hit = mc.player.raycast(range, 0f, false);
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();
            if (target instanceof LivingEntity && target.isAlive() && target != mc.player) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                ticks = cooldown;
            }
        }
    }

    private boolean isFalling() {
        return mc.player != null
            && !mc.player.isOnGround()
            && mc.player.getVelocity().y < 0
            && mc.player.fallDistance > 0.0F;
    }

    public double getRange() { return range; }
    public void setRange(double v) { this.range = Math.max(1, Math.min(6, v)); }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int v) { this.cooldown = Math.max(5, Math.min(30, v)); }
    public boolean isCritOnly() { return critOnly; }
    public void setCritOnly(boolean v) { this.critOnly = v; }
}
