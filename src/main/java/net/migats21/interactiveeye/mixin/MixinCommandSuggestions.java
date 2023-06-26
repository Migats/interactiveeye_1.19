package net.migats21.interactiveeye.mixin;

import com.mojang.brigadier.ParseResults;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandSuggestions.class)
public abstract class MixinCommandSuggestions {

    @ModifyArg(method = "renderUsage(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"), index = 1)
    public FormattedCharSequence stylize(FormattedCharSequence formattedCharSequence) {
        if (formattedCharSequence instanceof MutableComponent mutableComponent) {
            return (FormattedCharSequence) mutableComponent.withStyle(GlobalHudScreen.font);
        }
        return formattedCharSequence;
    }
    @Shadow
    @Nullable
    private ParseResults<SharedSuggestionProvider> currentParse;
    @Inject(method = "formatChat", at = @At("HEAD"), cancellable = true)
    private void formatChat(String string, int i, CallbackInfoReturnable<FormattedCharSequence> cir) {
        cir.setReturnValue(this.currentParse != null ? CommandSuggestions.formatText(this.currentParse, string, i) : FormattedCharSequence.forward(string, GlobalHudScreen.font));
        cir.cancel();
    }
    @Redirect(method = "<clinit>", at=@At(value = "FIELD", target = "Lnet/minecraft/network/chat/Style;EMPTY:Lnet/minecraft/network/chat/Style;"))
    private static Style text() {
        return GlobalHudScreen.font;
    }
}
