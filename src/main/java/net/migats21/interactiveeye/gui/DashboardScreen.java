package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class DashboardScreen extends GlobalHudScreen {
    @Override
    protected void show() {
        render_cooldown = 0.0f;
    }

    @Override
    protected void render(PoseStack poseStack, float tickDelta, int width, int height) {
        int refreshRate = minecraft.getWindow().getRefreshRate();
        if (refreshRate > Minecraft.fps || render_cooldown < 200.0f) {
            ani_progress += tickDelta;
            if (Minecraft.fps > refreshRate) render_cooldown += tickDelta; else render_cooldown = 0;
            int hudHeight = 50;
            int animatedHudHeight = (int) (bezierCurveAnimation(Math.min(Math.min(ani_progress/8.0f, 10.0f - render_cooldown/20.0f), 1.0f), 0, 0.75f, 1.0f, 1.0f) * hudHeight);
            int x = width - 132;
            int y = 4;
            int hudWidth = 128;
            renderBackground(poseStack, x, y, hudWidth, animatedHudHeight);
            if (ani_progress > 8.0f && render_cooldown < 180.0f) {
                poseStack.translate(0.0, 0.0, 1000.0);
                poseStack.pushPose();
                poseStack.scale(4, 4, 4);
                Component styledData = Component.literal(String.valueOf(Minecraft.fps)).withStyle(font);
                int j = minecraft.font.draw(poseStack, styledData, x/4 + 1, y/4 + 2, 0xc0ffffff);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.scale(2, 2, 2);
                styledData = Component.literal("fps").withStyle(font);
                minecraft.font.draw(poseStack, styledData, j*2, y/2 + 12, 0xc0ffffff);;
                poseStack.popPose();
            }
        } else {
            ani_progress = 0.0f;
        }
    }
}
