package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.chat.ChatPreviewAnimator;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;

import java.util.List;
import java.util.Objects;

public class ModChatComponentScreen extends GlobalHudScreen {

    @Override
    protected void show() {

    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
    }

    private int getLineHeight() {
        Objects.requireNonNull(this.minecraft.font);
        return (int)(9.0 * ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0));
    }

    private static double getTimeFactor(int i) {
        double d = (double)i / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = Mth.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    private void drawTagIcon(PoseStack poseStack, int i, int j, GuiMessageTag.Icon icon) {
        int k = j - icon.height - 1;
        icon.draw(poseStack, i, k);
    }

    @Override
    protected void render(PoseStack poseStack, float deltaFrameTime, int width, int height) {
        boolean bl = minecraft.gui.chat.getFocusedChat() != null;
        if (!this.isChatHidden()) {
            poseStack.pushPose();
            poseStack.translate(0.0, height + getChatOffset() - 48, -50.0);
            int j = minecraft.gui.chat.getLinesPerPage();
            int k = minecraft.gui.chat.trimmedMessages.size();
            if (k > 0) {
                float f = (float) minecraft.gui.chat.getScale();
                int l = Mth.ceil((float) minecraft.gui.chat.getWidth() / f);
                poseStack.pushPose();
                poseStack.translate(4.0, 8.0, 0.0);
                poseStack.scale(f, f, 1.0F);
                double d = minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612;
                double e = minecraft.options.textBackgroundOpacity().get();
                double g = minecraft.options.chatLineSpacing().get();
                int m = this.getLineHeight();
                double h = -8.0 * (g + 1.0) + 4.0 * g;
                int n = 0;
                int p;
                int r;
                int s;
                int t;
                int u;
                int v;
                for(int o = 0; o + minecraft.gui.chat.chatScrollbarPos < minecraft.gui.chat.trimmedMessages.size() && o < j; ++o) {
                    RenderSystem.enableDepthTest();
                    GuiMessage.Line line = minecraft.gui.chat.trimmedMessages.get(o + minecraft.gui.chat.chatScrollbarPos);
                    if (line != null) {
                        p = minecraft.gui.tickCount - line.addedTime();
                        if (p < 200 || bl) {
                            double q = bl ? 1.0 : getTimeFactor(p);
                            r = (int)(255.0 * q * d);
                            s = (int)(255.0 * q * e);
                            t = 0x458a48;
                            if (minecraft.screen instanceof AbstractContainerScreen<?>) {
                                r = Math.max(r, 224);
                                s = Math.max(s, 224);
                                t = 0xf2e11;
                            }
                            ++n;
                            if (r > 3) {
                                u = -o * m;
                                v = (int)((double)u + h);
                                poseStack.pushPose();
                                poseStack.translate(0.0, 0.0, 50.0);
                                fill(poseStack, -4, u - m, l + 8, u, s << 24 | t);
                                GuiMessageTag guiMessageTag = line.tag();
                                if (guiMessageTag != null) {
                                    int w = (guiMessageTag.text() == null ? 0x81e386 : guiMessageTag.indicatorColor()) | r << 24;
                                    fill(poseStack, -4, u - m, -2, u, w);
                                    if (bl && line.endOfEntry() && guiMessageTag.icon() != null) {
                                        int x = minecraft.font.width(line.content()) + 4;
                                        Objects.requireNonNull(minecraft.font);
                                        int y = v + 9;
                                        this.drawTagIcon(poseStack, x, y, guiMessageTag.icon());
                                    }
                                }

                                RenderSystem.enableBlend();
                                poseStack.translate(0.0, 0.0, 1.0);
                                minecraft.font.drawShadow(poseStack, line.content(), 0.0F, (float)v, 16777215 + (r << 24));
                                RenderSystem.disableBlend();
                                poseStack.popPose();
                            }
                        }
                    }
                }

                long z = minecraft.getChatListener().queueSize();
                int aa;
                if (z > 0L) {
                    p = (int)(128.0 * d);
                    aa = (int)(255.0 * e);
                    poseStack.pushPose();
                    poseStack.translate(0.0, 0.0, 50.0);
                    fill(poseStack, -2, 0, l + 4, 9, aa << 24);
                    RenderSystem.enableBlend();
                    poseStack.translate(0.0, 0.0, 50.0);
                    minecraft.font.drawShadow(poseStack, Component.translatable("chat.queue", z), 0.0F, 1.0F, 16777215 + (p << 24));
                    poseStack.popPose();
                    RenderSystem.disableBlend();
                }

                if (bl) {
                    p = this.getLineHeight();
                    aa = k * p;
                    int ab = n * p;
                    r = minecraft.gui.chat.chatScrollbarPos * ab / k;
                    s = ab * ab / aa;
                    if (aa != ab) {
                        t = r > 0 ? 170 : 96;
                        u = minecraft.gui.chat.newMessageSinceScroll ? 13382451 : 0x333333;
                        v = l + 4;
                        fill(poseStack, v, -r, v + 2, -r - s, u + (t << 24));
                        fill(poseStack, v + 2, -r, v + 1, -r - s, 13421772 + (t << 24));
                    }
                }

                poseStack.popPose();
            }
            RenderSystem.disableDepthTest();
            poseStack.popPose();
        }
        if (bl) {
            renderChatBox((ChatScreen)minecraft.screen, poseStack, (int)(minecraft.mouseHandler.xpos() * (double)minecraft.getWindow().getGuiScaledWidth() / (double)minecraft.getWindow().getScreenWidth()), (int)(minecraft.mouseHandler.ypos() * (double)minecraft.getWindow().getGuiScaledHeight() / (double)minecraft.getWindow().getScreenHeight()), deltaFrameTime);
        }
    }

    public void renderChatBox(ChatScreen screen, PoseStack poseStack, int mouseX, int mouseY, float deltaFrameTime) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 60.0);
        screen.setFocused(screen.input);
        screen.input.setFocus(true);
        fill(poseStack, 2, screen.height - 14, screen.width - 2, screen.height - 2, 0xe00f2e11);
        screen.input.render(poseStack, mouseX, mouseY, deltaFrameTime);
        for (Widget widget : screen.renderables) {
            widget.render(poseStack, mouseX, mouseY, deltaFrameTime);
        }

        boolean bl = minecraft.getProfileKeyPairManager().signer() != null;
        ChatPreviewAnimator.State state = screen.chatPreviewAnimator.get(Util.getMillis(), screen.getDisplayedPreviewText());
        if (state.preview() != null) {

            float f = state.alpha();
            int i = (int)(255.0 * ((Double) minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612) * (double)f);
            int k = screen.chatPreviewWidth();
            List<FormattedCharSequence> list = minecraft.font.split(state.preview(), screen.chatPreviewWidth());
            int l = screen.chatPreviewHeight(list);
            int m = screen.chatPreviewTop(l);
            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate((double) screen.chatPreviewLeft(), (double)m, 0.0);
            fill(poseStack, 0, 0, k, l, 0xe00f2e11);
            int n;
            if (i > 0) {
                poseStack.translate(2.0, 2.0, 0.0);

                for(n = 0; n < list.size(); ++n) {
                    FormattedCharSequence formattedCharSequence = list.get(n);
                    if (formattedCharSequence instanceof MutableComponent mutableComponent) {
                        mutableComponent.withStyle(GlobalHudScreen.font);
                    }
                    Objects.requireNonNull(minecraft.font);
                    int o = n * 9;
                    screen.renderChatPreviewHighlights(poseStack, formattedCharSequence, o, i);
                    minecraft.font.drawShadow(poseStack, formattedCharSequence, 0.0F, (float)o, i << 24 | 16777215);
                }
            }

            poseStack.popPose();
            RenderSystem.disableBlend();
            if (bl && screen.getChatPreview().peek() != null) {
                n = screen.getChatPreview().hasScheduledRequest() ? 15118153 : 0x81e386;
                int p = (int)(255.0F * f);
                poseStack.pushPose();
                fill(poseStack, 0, m, 2, screen.chatPreviewBottom(), p << 24 | n);
                poseStack.popPose();
            }

            screen.commandSuggestions.renderSuggestions(poseStack, mouseX, mouseY);
        } else {
            screen.commandSuggestions.render(poseStack, mouseX, mouseY);
            if (bl) {
                poseStack.pushPose();
                fill(poseStack, 0, screen.height - 14, 2, screen.height - 2, -8932375);
                poseStack.popPose();
            }
        }

        Style style = screen.getComponentStyleAt((double)mouseX, (double)mouseY);
        if (style != null && style.getHoverEvent() != null) {
            screen.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
        } else {
            GuiMessageTag guiMessageTag = minecraft.gui.getChat().getMessageTagAt((double)mouseX, (double)mouseY);
            if (guiMessageTag != null && guiMessageTag.text() != null) {
                screen.renderTooltip(poseStack, minecraft.font.split(guiMessageTag.text(), 260), mouseX, mouseY);
            }
        }
        poseStack.popPose();
    }

    public static int getChatOffset() {
        if (minecraft.player.isSpectator()) {
            return minecraft.gui.chat.getFocusedChat() == null ? 38 : 24;
        }
        if ((minecraft.screen instanceof AbstractContainerScreen<?>)) {
            return 38;
        }
        if (minecraft.player.isCreative()) {
            return 16;
        }
        if (minecraft.player.getArmorValue() > 0) {
            return -10;
        }
        return 0;
    }
}
