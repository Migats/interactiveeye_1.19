package net.migats21.interactiveeye.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    public static final String KEYCATEGORY_INTERACTIVEEYE = "key.category.interactiveeye";
    public static final String KEY_INSPECT = "key.interactiveeye.inspect";

    public static KeyBinding key_inspect;

    public static void register() {
        key_inspect = KeyBindingHelper.registerKeyBinding(new KeyBinding(KEY_INSPECT, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KEYCATEGORY_INTERACTIVEEYE));
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (key_inspect.wasPressed() && !InspectionScreen.inspecting) {
                InspectionScreen.inspect();
            }
            InspectionScreen.inspecting = key_inspect.isPressed();
        });
    }
}
