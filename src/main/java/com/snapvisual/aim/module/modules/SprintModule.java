package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;

public class SprintModule extends Module {
    public SprintModule() {
        super("Авто спринт", "Автоматический спринт при беге", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.isOnGround() && !mc.player.isSprinting() && mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }
    }
}
