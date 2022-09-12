package net.migats21.interactiveeye;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.migats21.interactiveeye.input.KeyInputHandler;
import net.migats21.interactiveeye.util.StringMappings;

public class InteractiveEyeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        HudRenderCallback.EVENT.register(new InspectionScreen());
        StringMappings.init();
    }
}
