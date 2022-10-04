package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;

public class WarningScreen extends GlobalHudScreen {

    private float healthDanger;

    private DamageSource cause;

    public WarningScreen() {
        ClientTickEvents.END_WORLD_TICK.register(this::tick);
    }
    @Override
    @Deprecated
    protected void show() {
        throw new IllegalCallerException("Medhod show cannot be called on WarningScreen");
    }
    @Override
    protected void render(PoseStack poseStack, float deltaFrameTime, int width, int height) {
        if (cause != null) {
            int x = 4;
            int y = height/2 - 24;
            renderBackground(poseStack, x, y, 160, 48);
        }
    }

    protected void setAlert(float f) {
        this.healthDanger = f;
    }

    public void tick(ClientLevel level) {
        cause = null;
        if (healthDanger > 0.0f && minecraft.player.getHealth() < healthDanger) {
            cause = minecraft.player.getLastDamageSource();
            return;
        }
        if (minecraft.player.fallDistance > 0) {
            int i = minecraft.player.calculateFallDamage(minecraft.player.fallDistance, 1.0f);
            if (i > minecraft.player.getHealth() && i > 6.0f) {
                cause = DamageSource.FALL;
            }
        }
    }
}
