package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.InteractiveEye;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public abstract class GlobalHudScreen extends GuiComponent {
    private static GlobalHudScreen inspectionScreen;
    protected static Style font;
    protected static Minecraft minecraft;
    private static GlobalHudScreen dashboardScreen;
    private static WarningScreen warningScreen;
    protected float ani_progress;
    protected float render_cooldown;

    public static void init() {
        minecraft = Minecraft.getInstance();
        font = Style.EMPTY.withFont(new ResourceLocation(InteractiveEye.MODID, "rounded"));
        inspectionScreen = new InspectionScreen();
        dashboardScreen = new DashboardScreen();
        warningScreen = new WarningScreen();
    }

    public static void renderAll(PoseStack poseStack, float deltaFrameTime) {
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        inspectionScreen.render(poseStack, deltaFrameTime, width, height);
        dashboardScreen.render(poseStack, deltaFrameTime, width, height);
        warningScreen.render(poseStack, deltaFrameTime, width, height);
    }

    // Bezier curve formula provided by technobroken
    protected static float bezierCurveAnimation(float t, float c0, float c1, float c2, float c3) {
        return (float) (Math.pow(t,3)*(c0+3.0f*c1-3.0f*c2+c3) + Math.pow(t,2)*(3.0f*c0-6.0f*c1+3.0f*c2)+t*(-3.0f*c0+3.0f*c1)+c0);
    }

    public static void showInspect() {
        inspectionScreen.show();
    }

    public static void showDashboard() {
        dashboardScreen.show();
    }

    public static void showWarning() {
        warningScreen.setAlert(5.0f);
    }

    public static void hideWarning() {
        warningScreen.setAlert(0.0f);
    }

    protected abstract void show();

    protected abstract void render(PoseStack poseStack, float deltaFrameTime, int width, int height);

    protected void renderBackground(PoseStack poseStack, int x, int y, int width, int height) {
        fill(poseStack, x, y + (height > 0 ? -1 : 1), x + width, y, 0xff81e386);
        fill(poseStack, x, y, x + width, y + height, minecraft.screen == null ? 0x40458a48 : 0xe00f2e11);
        fill(poseStack, x, y + height, x + width, y + height + (height > 0 ? 1 : -1), 0xff81e386);
    }
}
