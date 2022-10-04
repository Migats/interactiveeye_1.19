package net.migats21.interactiveeye.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    public static final String KEYCATEGORY_INTERACTIVEEYE = "key.category.interactiveeye";
    public static final String KEY_INSPECT = "key.interactiveeye.inspect";
    public static final String KEY_DASHBOARD = "key.interactiveeye.dashboard";
    public static final String KEY_ALERT_TEMP = "key.interactiveeye.alert";

    public static KeyMapping key_inspect;
    public static KeyMapping key_dashboard;
    private static KeyMapping key_alert_temp;
    private static final Minecraft minecraft = Minecraft.getInstance();
    public static void register() {
        key_inspect = KeyBindingHelper.registerKeyBinding(new KeyMapping(KEY_INSPECT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYCATEGORY_INTERACTIVEEYE));
        key_dashboard = KeyBindingHelper.registerKeyBinding(new KeyMapping(KEY_DASHBOARD, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, KEYCATEGORY_INTERACTIVEEYE));
        key_alert_temp = KeyBindingHelper.registerKeyBinding(new KeyMapping(KEY_ALERT_TEMP, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KEYCATEGORY_INTERACTIVEEYE));
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (minecraft.screen == null || !(minecraft.screen instanceof CreativeModeInventoryScreen creativeScreen && creativeScreen.getSelectedTab() == CreativeModeTab.TAB_SEARCH.getId()) && !(minecraft.screen instanceof AnvilScreen anvilScreen && anvilScreen.name.canConsumeInput())) {
                boolean pressed = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(key_inspect).getValue());
                if (pressed) GlobalHudScreen.showInspect();
                InspectionScreen.inspecting = pressed;
                pressed = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(key_dashboard).getValue());
                if (pressed) GlobalHudScreen.showDashboard();
                pressed = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(key_alert_temp).getValue());
                if (pressed) GlobalHudScreen.showWarning(); else GlobalHudScreen.hideWarning();
            }
        });
    }
}
