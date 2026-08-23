package com.snapvisual.aim;

import com.snapvisual.aim.gui.SnapAimScreen;
import com.snapvisual.aim.module.ModuleManager;
import com.snapvisual.aim.module.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapAimMod implements ClientModInitializer {
    public static final String MOD_ID = "snapaim";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftClient MC;
    public static KeyBinding OPEN_GUI_KEY;

    @Override
    public void onInitializeClient() {
        LOGGER.info("SnapAim by @snapclient initialized");
        MC = MinecraftClient.getInstance();

        OPEN_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.snapaim.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.snapaim.main"
        ));

        ModuleManager.registerModule(new AutoAimModule());
        ModuleManager.registerModule(new TriggerBotModule());
        ModuleManager.registerModule(new FOVModule());
        ModuleManager.registerModule(new SprintModule());
        ModuleManager.registerModule(new FullBrightModule());
        ModuleManager.registerModule(new NoSlowModule());
        ModuleManager.registerModule(new ReachModule());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (OPEN_GUI_KEY.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new SnapAimScreen());
                } else if (client.currentScreen instanceof SnapAimScreen) {
                    client.setScreen(null);
                }
            }
        });
    }
}
