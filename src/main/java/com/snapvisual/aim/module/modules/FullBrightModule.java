package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;

public class FullBrightModule extends Module {
    private double oldGamma = 1.0;

    public FullBrightModule() {
        super("Полная яркость", "Максимальная яркость без темноты", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            oldGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(100.0);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(oldGamma);
        }
    }
}
