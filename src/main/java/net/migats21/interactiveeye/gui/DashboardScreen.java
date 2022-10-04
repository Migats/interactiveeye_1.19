package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.regex.Pattern;

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
            int hudHeight = 48;
            int animatedHudHeight = (int) (bezierCurveAnimation(Math.min(Math.min(ani_progress/8.0f, 10.0f - render_cooldown/20.0f), 1.0f), 0, 0.75f, 1.0f, 1.0f) * hudHeight);
            int x = width - 200;
            int y = 4;
            int hudWidth = 196;
            renderBackground(poseStack, x, y, hudWidth, animatedHudHeight);
            if (ani_progress > 8.0f && render_cooldown < 180.0f) {
                poseStack.translate(0.0, 0.0, 1000.0);
                poseStack.pushPose();
                poseStack.scale(4, 4, 4);
                Component styledData = Component.literal(String.valueOf(Math.min(Minecraft.fps, 999))).withStyle(font);
                int j = minecraft.font.draw(poseStack, styledData, x/4f + 2, y/4f + 2.5f, 0xc0ffffff);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                styledData = Component.literal("fps").withStyle(font);
                minecraft.font.draw(poseStack, styledData, j*2, y/2f + 12, 0xc0ffffff);;
                poseStack.popPose();
                //float f = minecraft.player.connection.getConnection().getAverageSentPackets();
                //float g = minecraft.player.connection.getConnection().getAverageReceivedPackets();
                styledData = Component.literal(String.format(Locale.ROOT, "Chunks: %d/%d", minecraft.levelRenderer.countRenderedChunks(), minecraft.levelRenderer.viewArea.chunks.length)).withStyle(font);
                j = width - 8 - minecraft.font.width(styledData);
                minecraft.font.draw(poseStack, styledData, j, y + 7, 0xc0ffffff);
                styledData = Component.literal(String.format(Locale.ROOT, "Entities: %d/%d", minecraft.levelRenderer.renderedEntities, minecraft.level.getEntityCount())).withStyle(font);
                j = width - 8 - minecraft.font.width(styledData);
                minecraft.font.draw(poseStack, styledData, j, y + 7 + minecraft.font.lineHeight, 0xc0ffffff);
                styledData = Component.literal(String.format(Locale.ROOT, "Particles: %s", minecraft.particleEngine.countParticles())).withStyle(font);
                j = width - 8 - minecraft.font.width(styledData);
                minecraft.font.draw(poseStack, styledData, j, y + 7 + minecraft.font.lineHeight * 2, 0xc0ffffff);
                IntegratedServer server = minecraft.getSingleplayerServer();
                if (server != null) {
                    styledData = Component.literal(String.format(Locale.ROOT, "%.0fms/tick", server.getAverageTickTime())).withStyle(font);
                    j = width - 8 - minecraft.font.width(styledData);
                    minecraft.font.draw(poseStack, styledData, j, y + 7 + minecraft.font.lineHeight*3, 0xc0ffffff);
                } else {
                    PlayerInfo playerInfo = minecraft.player.connection.getPlayerInfo(minecraft.player.getUUID());
                    if (playerInfo != null) {
                        styledData = Component.literal(String.format(Locale.ROOT, "%dms ping", playerInfo.getLatency())).withStyle(font);
                        j = width - 8 - minecraft.font.width(styledData);
                        minecraft.font.draw(poseStack, styledData, j, y + 7 + minecraft.font.lineHeight*3, 0xc0ffffff);
                    }
                }
            }
        } else {
            ani_progress = 0.0f;
        }
    }
}
