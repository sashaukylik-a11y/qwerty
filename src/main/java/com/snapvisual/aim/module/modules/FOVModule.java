package com.snapvisual.aim.module.modules;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;

public class FOVModule extends Module {
    private float radius = 80f;
    private boolean visible = true;

    public FOVModule() {
        super("FOV круг", "Визуальный радиус наводки", Category.VISUAL);
    }

    public float getRadius() { return radius; }
    public void setRadius(float v) { this.radius = Math.max(20, Math.min(200, v)); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean v) { this.visible = v; }
}
