package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;

public class ReachModule extends Module {
    private double reach = 3.5;

    public ReachModule() {
        super("Длинные руки", "Увеличенная дистанция атаки", Category.COMBAT);
    }

    public double getReach() { return reach; }
    public void setReach(double v) { this.reach = Math.max(3.0, Math.min(6.0, v)); }
}
