package net.migats21.interactiveeye.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migats21.interactiveeye.gui.GlobalHudScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSuggestions.SuggestionsList.class)
public class MixinCommandSuggestionList {
    @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"))
    public int stylize(Font instance, PoseStack poseStack, String string, float f, float g, int i) {
        FormattedCharSequence charSequence = FormattedCharSequence.forward(string, GlobalHudScreen.font);
        return instance.drawShadow(poseStack, charSequence, f, g, i);
    }
}
