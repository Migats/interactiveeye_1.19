package net.migats21.interactiveeye;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.migats21.interactiveeye.input.KeyInputHandler;
import net.migats21.interactiveeye.util.StringMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractiveEye implements ClientModInitializer {
    public static final String MODID = "interactiveeye";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        StringMappings.init();
        GlobalHudScreen.init();
    }
}
