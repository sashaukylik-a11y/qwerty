package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;

public class NoSlowModule extends Module {
    public NoSlowModule() {
        super("Анти-замедление", "Без замедления от еды/лука/щита", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.isUsingItem()) {
            mc.player.setVelocity(mc.player.getVelocity().multiply(1.0, 1.0, 1.0));
        }
    }
}
