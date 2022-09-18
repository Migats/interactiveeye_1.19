package net.migats21.interactiveeye.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.gui.InspectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameGlobalHud implements ResourceManagerReloadListener, AutoCloseable {
    @Final
    @Shadow
    private Minecraft minecraft;
    @Inject(method = "render(FJZ)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void render(float f, long l, boolean bl, CallbackInfo info) {
        PoseStack poseStack = new PoseStack();
        InspectionScreen.render(poseStack, minecraft.getDeltaFrameTime());
    }
}
