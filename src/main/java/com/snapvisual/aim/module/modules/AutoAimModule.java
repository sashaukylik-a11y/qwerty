package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;
import com.snapvisual.aim.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class AutoAimModule extends Module {
    private double range = 4.5;
    private float fov = 120f;
    private int smooth = 15;
    private boolean critOnly = true;
    private boolean players = true;
    private boolean mobs = true;
    private boolean animals = false;

    public AutoAimModule() {
        super("Авто наводка", "Плавная наводка на цель в прыжке", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;

        if (critOnly && !isJumping()) return;

        Entity target = findTarget();
        if (target == null) return;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.75, 0);
        float[] rotations = RotationUtil.getRotations(eyePos, targetPos);

        float factor = smooth / 200f;
        float[] smoothed = RotationUtil.smoothRotation(
            mc.player.getYaw(), mc.player.getPitch(),
            rotations[0], rotations[1], factor
        );

        mc.player.setYaw(smoothed[0]);
        mc.player.setPitch(smoothed[1]);
    }

    private boolean isJumping() {
        return mc.player != null && !mc.player.isOnGround() && mc.player.getVelocity().y > 0;
    }

    private Entity findTarget() {
        if (mc.player == null || mc.world == null) return null;
        Box box = new Box(mc.player.getBlockPos()).expand(range + 1);
        List<Entity> entities = mc.world.getOtherEntities(mc.player, box, e -> {
            if (!(e instanceof LivingEntity)) return false;
            if (e == mc.player) return false;
            if (!e.isAlive()) return false;
            if (mc.player.distanceTo(e) > range) return false;
            float[] rots = RotationUtil.getRotations(mc.player.getEyePos(), e.getPos().add(0, e.getHeight() * 0.5, 0));
            float yawDiff = Math.abs(RotationUtil.normalizeAngle(rots[0] - mc.player.getYaw()));
            if (yawDiff > fov / 2) return false;
            if (e instanceof PlayerEntity && !players) return false;
            if (e instanceof HostileEntity && !mobs) return false;
            if (e instanceof PassiveEntity && !animals) return false;
            return true;
        });
        return entities.stream()
            .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    public double getRange() { return range; }
    public void setRange(double v) { this.range = Math.max(1, Math.min(6, v)); }
    public float getFov() { return fov; }
    public void setFov(float v) { this.fov = Math.max(30, Math.min(180, v)); }
    public int getSmooth() { return smooth; }
    public void setSmooth(int v) { this.smooth = Math.max(1, Math.min(100, v)); }
    public boolean isCritOnly() { return critOnly; }
    public void setCritOnly(boolean v) { this.critOnly = v; }
    public boolean getPlayers() { return players; }
    public void setPlayers(boolean v) { this.players = v; }
    public boolean getMobs() { return mobs; }
    public void setMobs(boolean v) { this.mobs = v; }
    public boolean getAnimals() { return animals; }
    public void setAnimals(boolean v) { this.animals = v; }
}
