package net.migats21.interactiveeye.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Shadow
    public EditBox input;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At("HEAD"), cancellable = true)
    public void dontRender(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "init()V", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        this.input.setFormatter((string, integer) -> FormattedCharSequence.forward(string, GlobalHudScreen.font));
    }
    @ModifyArg(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/client/gui/Font;ZZIIZI)V"), index = 9)
    public int initBackground(int i) {
        return 0xe00f2e11;
    }
}
