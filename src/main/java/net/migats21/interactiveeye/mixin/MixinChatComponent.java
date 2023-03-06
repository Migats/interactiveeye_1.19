package net.migats21.interactiveeye.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.migats21.interactiveeye.gui.ModChatComponentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class MixinChatComponent {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", at = @At("HEAD"), cancellable = true)
    public void dontRender(PoseStack poseStack, int i, CallbackInfo ci) {
        ci.cancel();
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Component addMessageStyle(Component component) {
        if (component instanceof MutableComponent mutableComponent) {
            return mutableComponent.withStyle(GlobalHudScreen.font);
        }
        return component;
    }
    @Inject(method = "screenToChatY(D)D", at = @At("HEAD"), cancellable = true)
    public void screenToChatY(double d, CallbackInfoReturnable<Double> cir) {
        double e = (double)this.minecraft.getWindow().getGuiScaledHeight() + ModChatComponentScreen.getChatOffset() - d - 40.0;
        cir.setReturnValue(e / (((double)this.minecraft.options.chatScale().get()) * ((double)this.minecraft.options.chatLineSpacing().get() + 1.0)));
        cir.cancel();
    }
}
