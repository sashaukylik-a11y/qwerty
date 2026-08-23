package com.snapvisual.aim.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static float[] getRotations(Vec3d from, Vec3d to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new float[] { yaw, pitch };
    }

    public static float normalizeAngle(float angle) {
        angle = angle % 360f;
        if (angle > 180f) angle -= 360f;
        if (angle < -180f) angle += 360f;
        return angle;
    }

    public static float[] smoothRotation(float currentYaw, float currentPitch, float targetYaw, float targetPitch, float factor) {
        float yawDiff = normalizeAngle(targetYaw - currentYaw);
        float pitchDiff = normalizeAngle(targetPitch - currentPitch);
        float newYaw = currentYaw + yawDiff * factor;
        float newPitch = MathHelper.clamp(currentPitch + pitchDiff * factor, -90f, 90f);
        return new float[] { newYaw, newPitch };
    }

    public static double getAngleToEntity(Entity target) {
        if (mc.player == null || target == null) return 360;
        float[] targetRot = getRotations(mc.player.getEyePos(), target.getEyePos());
        float yawDiff = Math.abs(normalizeAngle(targetRot[0] - mc.player.getYaw()));
        float pitchDiff = Math.abs(normalizeAngle(targetRot[1] - mc.player.getPitch()));
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }
}
