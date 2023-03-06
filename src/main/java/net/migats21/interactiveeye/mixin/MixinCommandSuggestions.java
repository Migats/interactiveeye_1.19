package net.migats21.interactiveeye.mixin;

import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CommandSuggestions.class)
public class MixinCommandSuggestions {
    @ModifyArg(method = "renderUsage(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"), index = 1)
    public FormattedCharSequence stylize(FormattedCharSequence formattedCharSequence) {
        if (formattedCharSequence instanceof MutableComponent mutableComponent) {
            return (FormattedCharSequence) mutableComponent.withStyle(GlobalHudScreen.font);
        }
        return formattedCharSequence;
    }
}
