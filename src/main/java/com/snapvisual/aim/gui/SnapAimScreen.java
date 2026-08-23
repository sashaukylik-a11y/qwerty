package com.snapvisual.aim.gui;

import com.snapvisual.aim.module.Category;
import com.snapvisual.aim.module.Module;
import com.snapvisual.aim.module.ModuleManager;
import com.snapvisual.aim.module.modules.*;
import com.snapvisual.aim.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SnapAimScreen extends Screen {
    private Category selectedCategory = Category.COMBAT;
    private Module selectedModule = null;

    private static final int BG_DARK = 0xDD0B0B10;
    private static final int BG_PANEL = 0xFF12121A;
    private static final int BG_CARD = 0xFF181820;
    private static final int BORDER = 0xFF282832;
    private static final int BORDER_GLOW = 0xFFB43232;
    private static final int TEXT_PRIMARY = 0xFFEBEBF5;
    private static final int TEXT_SECOND = 0xFF8282A0;
    private static final int ACCENT = 0xFFDC3C3C;
    private static final int TOGGLE_ON = 0xFFDC3C3C;
    private static final int TOGGLE_OFF = 0xFF282837;

    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    private int guiX = 80, guiY = 100;
    private final int guiW = 420, guiH = 260;

    private final List<Widget> widgets = new ArrayList<>();

    public SnapAimScreen() {
        super(Text.literal("SnapAim"));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x55000000);
        RenderUtil.fillRounded(ctx, guiX, guiY, guiW, guiH, 10, BG_DARK);
        RenderUtil.drawOutline(ctx, guiX, guiY, guiW, guiH, 10, BORDER);

        RenderUtil.fillRoundedTop(ctx, guiX, guiY, guiW, 36, 10, BG_PANEL);
        drawPulseIcon(ctx, guiX + 12, guiY + 11);
        ctx.drawTextWithShadow(textRenderer, "@snapclient", guiX + 34, guiY + 8, ACCENT);
        ctx.drawText(textRenderer, "tg - @snapclient", guiX + 34, guiY + 20, TEXT_SECOND, false);
        ctx.drawText(textRenderer, "×", guiX + guiW - 16, guiY + 10, 0xFFFF4444, false);

        int tabX = guiX + 12;
        int tabY = guiY + 42;
        for (Category cat : Category.values()) {
            boolean sel = cat == selectedCategory;
            String label = cat.getDisplayName();
            int color = sel ? ACCENT : TEXT_SECOND;
            ctx.drawText(textRenderer, label, tabX, tabY, color, false);
            if (sel) {
                int tw = textRenderer.getWidth(label);
                ctx.fill(tabX, tabY + 11, tabX + tw, tabY + 13, ACCENT);
            }
            tabX += textRenderer.getWidth(label) + 18;
        }

        int listX = guiX + 12;
        int listY = guiY + 62;
        int listW = selectedModule == null ? guiW - 24 : (guiW - 24) / 2 - 6;

        for (Module m : ModuleManager.getModulesByCategory(selectedCategory)) {
            boolean enabled = m.isEnabled();
            int bg = enabled ? RenderUtil.blend(BG_CARD, TOGGLE_ON, 0.1f) : BG_CARD;
            RenderUtil.fillRounded(ctx, listX, listY, listW, 24, 4, bg);
            RenderUtil.drawOutline(ctx, listX, listY, listW, 24, 4, enabled ? BORDER_GLOW : BORDER);
            ctx.drawText(textRenderer, m.getName(), listX + 10, listY + 7, TEXT_PRIMARY, false);
            int pillX = listX + listW - 40;
            int pillY = listY + 5;
            RenderUtil.fillRounded(ctx, pillX, pillY, 32, 14, 7, enabled ? TOGGLE_ON : TOGGLE_OFF);
            int dotX = enabled ? pillX + 18 : pillX + 2;
            ctx.fill(dotX, pillY + 2, dotX + 10, pillY + 12, 0xFFFFFFFF);
            listY += 30;
        }

        if (selectedModule != null) {
            int setX = listX + listW + 10;
            int setY = guiY + 62;
            int setW = (guiW - 24) / 2 - 6;
            int setH = guiH - 76;
            RenderUtil.fillRounded(ctx, setX, setY, setW, setH, 6, BG_CARD);
            RenderUtil.drawOutline(ctx, setX, setY, setW, setH, 6, BORDER);
            ctx.drawTextWithShadow(textRenderer, selectedModule.getName(), setX + 8, setY + 6, ACCENT);
            ctx.drawText(textRenderer, selectedModule.getDescription(), setX + 8, setY + 18, TEXT_SECOND, false);
            if (widgets.isEmpty()) buildWidgets(setX + 8, setY + 32, setW - 16);
            for (Widget w : widgets) w.render(ctx, mx, my);
        } else {
            widgets.clear();
        }
        super.render(ctx, mx, my, delta);
    }

    private void buildWidgets(int x, int y, int w) {
        widgets.clear();
        int cy = y;
        if (selectedModule instanceof AutoAimModule aim) {
            widgets.add(new Slider(x, cy, w, "Дистанция", aim.getRange(), 1.0, 6.0, v -> aim.setRange(v.doubleValue())));
            cy += 28;
            widgets.add(new Slider(x, cy, w, "Угол обзора", aim.getFov(), 30f, 180f, v -> aim.setFov(v.floatValue())));
            cy += 28;
            widgets.add(new Slider(x, cy, w, "Плавность", aim.getSmooth(), 1, 100, v -> aim.setSmooth(v.intValue())));
            cy += 28;
            widgets.add(new Toggle(x, cy, w, "Только крит", aim.isCritOnly(), aim::setCritOnly));
            cy += 22;
            widgets.add(new Toggle(x, cy, w, "Игроки", aim.getPlayers(), aim::setPlayers));
            cy += 22;
            widgets.add(new Toggle(x, cy, w, "Мобы", aim.getMobs(), aim::setMobs));
            cy += 22;
            widgets.add(new Toggle(x, cy, w, "Животные", aim.getAnimals(), aim::setAnimals));
        } else if (selectedModule instanceof TriggerBotModule tb) {
            widgets.add(new Slider(x, cy, w, "Дистанция", tb.getRange(), 1.0, 6.0, v -> tb.setRange(v.doubleValue())));
            cy += 28;
            widgets.add(new Slider(x, cy, w, "Задержка", tb.getCooldown(), 5, 30, v -> tb.setCooldown(v.intValue())));
            cy += 28;
            widgets.add(new Toggle(x, cy, w, "Только крит", tb.isCritOnly(), tb::setCritOnly));
        } else if (selectedModule instanceof FOVModule fov) {
            widgets.add(new Slider(x, cy, w, "Радиус", fov.getRadius(), 20f, 200f, v -> fov.setRadius(v.floatValue())));
            cy += 28;
            widgets.add(new Toggle(x, cy, w, "Видимость", fov.isVisible(), fov::setVisible));
        } else if (selectedModule instanceof ReachModule reach) {
            widgets.add(new Slider(x, cy, w, "Дистанция", reach.getReach(), 3.0, 6.0, v -> reach.setReach(v.doubleValue())));
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && mx >= guiX && mx <= guiX + guiW && my >= guiY && my <= guiY + 36) {
            dragging = true;
            dragOffsetX = (int) (mx - guiX);
            dragOffsetY = (int) (my - guiY);
            return true;
        }
        if (button == 0 && mx >= guiX + guiW - 20 && mx <= guiX + guiW - 4 && my >= guiY + 8 && my <= guiY + 26) {
            this.close(); return true;
        }
        int tabX = guiX + 12;
        int tabY = guiY + 42;
        for (Category cat : Category.values()) {
            String label = cat.getDisplayName();
            int tw = textRenderer.getWidth(label);
            if (mx >= tabX && mx <= tabX + tw && my >= tabY && my <= tabY + 14) {
                selectedCategory = cat; selectedModule = null; widgets.clear(); return true;
            }
            tabX += tw + 18;
        }
        int listX = guiX + 12;
        int listY = guiY + 62;
        int listW = selectedModule == null ? guiW - 24 : (guiW - 24) / 2 - 6;
        for (Module m : ModuleManager.getModulesByCategory(selectedCategory)) {
            if (mx >= listX && mx <= listX + listW && my >= listY && my <= listY + 24) {
                int pillX = listX + listW - 40;
                if (mx >= pillX && mx <= pillX + 32) {
                    m.toggle(); widgets.clear();
                } else {
                    selectedModule = (selectedModule == m) ? null : m;
                    widgets.clear();
                }
                return true;
            }
            listY += 30;
        }
        for (Widget w : widgets) {
            if (w.mouseClicked(mx, my, button)) return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragging = false;
        for (Widget w : widgets) w.mouseReleased(mx, my, button);
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging) {
            guiX = (int) (mx - dragOffsetX);
            guiY = (int) (my - dragOffsetY);
            return true;
        }
        for (Widget w : widgets) {
            if (w.mouseDragged(mx, my, button, dx, dy)) return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { this.close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawPulseIcon(DrawContext ctx, int x, int y) {
        int[] xs = {x, x+2, x+5, x+8, x+11, x+14, x+16};
        int[] ys = {y+5, y+5, y, y+10, y+2, y+5, y+5};
        for (int i = 0; i < xs.length - 1; i++) {
            ctx.fill(xs[i], ys[i], xs[i+1], ys[i+1] + 1, ACCENT);
        }
    }

    abstract static class Widget {
        protected int x, y, w;
        Widget(int x, int y, int w) { this.x = x; this.y = y; this.w = w; }
        abstract void render(DrawContext ctx, int mx, int my);
        boolean mouseClicked(double mx, double my, int button) { return false; }
        void mouseReleased(double mx, double my, int button) {}
        boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    }

    static class Toggle extends Widget {
        private final String label;
        private boolean value;
        private final Consumer<Boolean> callback;
        Toggle(int x, int y, int w, String label, boolean value, Consumer<Boolean> callback) {
            super(x, y, w); this.label = label; this.value = value; this.callback = callback;
        }
        @Override
        void render(DrawContext ctx, int mx, int my) {
            ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, label, x, y, TEXT_PRIMARY, false);
            int pillX = x + w - 32;
            RenderUtil.fillRounded(ctx, pillX, y, 28, 12, 6, value ? TOGGLE_ON : TOGGLE_OFF);
            int dotX = value ? pillX + 16 : pillX + 2;
            ctx.fill(dotX, y + 2, dotX + 8, y + 10, 0xFFFFFFFF);
        }
        @Override
        boolean mouseClicked(double mx, double my, int button) {
            if (button == 0 && mx >= x + w - 32 && mx <= x + w && my >= y && my <= y + 14) {
                value = !value; callback.accept(value); return true;
            }
            return false;
        }
    }

    static class Slider extends Widget {
        private final String label;
        private double value, min, max;
        private final Consumer<Number> callback;
        private boolean dragging = false;
        Slider(int x, int y, int w, String label, double value, double min, double max, Consumer<Number> callback) {
            super(x, y, w); this.label = label; this.value = value; this.min = min; this.max = max; this.callback = callback;
        }
        @Override
        void render(DrawContext ctx, int mx, int my) {
            String fmt = (max - min > 10) ? "%.0f" : "%.1f";
            ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer, label + ": " + String.format(fmt, value), x, y, TEXT_PRIMARY, false);
            int barY = y + 12;
            int fill = (int) (((value - min) / (max - min)) * w);
            ctx.fill(x, barY + 2, x + w, barY + 6, TOGGLE_OFF);
            ctx.fill(x, barY + 2, x + Math.min(fill, w), barY + 6, ACCENT);
            int thumbX = x + Math.min(fill, w);
            ctx.fill(thumbX - 2, barY, thumbX + 2, barY + 8, 0xFFFFFFFF);
        }
        @Override
        boolean mouseClicked(double mx, double my, int button) {
            if (button == 0 && mx >= x && mx <= x + w && my >= y + 10 && my <= y + 20) {
                dragging = true; updateValue(mx); return true;
            }
            return false;
        }
        @Override
        boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
            if (dragging) { updateValue(mx); return true; }
            return false;
        }
        @Override
        void mouseReleased(double mx, double my, int button) {
            dragging = false;
        }
        private void updateValue(double mx) {
            double pct = Math.max(0, Math.min(1, (mx - x) / w));
            double newVal = min + pct * (max - min);
            if (max - min > 10) newVal = Math.round(newVal);
            else newVal = Math.round(newVal * 10) / 10.0;
            value = newVal;
            callback.accept(value);
        }
    }
}
