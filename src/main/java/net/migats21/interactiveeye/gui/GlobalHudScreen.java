package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.InteractiveEye;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class GlobalHudScreen extends GuiComponent {
    private static GlobalHudScreen inspectionScreen = new InspectionScreen();
    protected static Style font = Style.EMPTY.withFont(new ResourceLocation(InteractiveEye.MODID, "rounded"));
    protected static final Minecraft minecraft = Minecraft.getInstance();
    protected float ani_progress;
    private static final ShaderInstance SCREEN_SHUTTER_SHADER = createScreenShutterShader();

    private static ShaderInstance createScreenShutterShader() {
        try {
            return new ShaderInstance(minecraft.getResourceManager(), "screen_shutter", DefaultVertexFormat.POSITION_TEX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected ShaderInstance getScreenShutterShader() {
        return SCREEN_SHUTTER_SHADER;
    }

    public static void renderAll(PoseStack poseStack, float deltaFrameTime) {
        inspectionScreen.render(poseStack, deltaFrameTime);
    }

    // Bezier curve formula provided by technobroken
    protected static float bezierCurveAnimation(float t, float c0, float c1, float c2, float c3) {
        return (float) (Math.pow(t,3)*(c0+3.0f*c1-3.0f*c2+c3) + Math.pow(t,2)*(3.0f*c0-6.0f*c1+3.0f*c2)+t*(-3.0f*c0+3.0f*c1)+c0);
    }

    protected abstract void render(PoseStack poseStack, float deltaFrameTime);

    protected void renderBackground(PoseStack poseStack, int x, int y, int width, int height) {
        fill(poseStack, x, y + (height > 0 ? -1 : 1), x + width, y, 0xff81e386);
        fill(poseStack, x, y, x + width, x + height, minecraft.screen == null ? 0x40458a48 : 0xe00f2e11);
        fill(poseStack, x, y + height, x + width, y + height + (height > 0 ? 1 : -1), 0xff81e386);
    }
}
