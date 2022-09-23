package net.migats21.interactiveeye.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    public static final String KEYCATEGORY_INTERACTIVEEYE = "key.category.interactiveeye";
    public static final String KEY_INSPECT = "key.interactiveeye.inspect";

    public static KeyMapping key_inspect;
    public static void register() {
        key_inspect = KeyBindingHelper.registerKeyBinding(new KeyMapping(KEY_INSPECT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYCATEGORY_INTERACTIVEEYE));
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            boolean pressed = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), KeyBindingHelper.getBoundKeyOf(key_inspect).getValue());
            if (pressed && !InspectionScreen.inspecting) {
                InspectionScreen.inspect();
            }
            InspectionScreen.inspecting = pressed;
        });
    }
}
