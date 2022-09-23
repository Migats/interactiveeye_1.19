package net.migats21.interactiveeye.gui;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.migats21.interactiveeye.InteractiveEye;
import net.migats21.interactiveeye.util.StringMappings;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class InspectionScreen {
    public static boolean inspecting;
    private static final List<String> inline_data = Lists.newArrayList();

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static float ani_progress;
    private static final ShaderInstance SCREEN_SHUTTER_SHADER = createScreenShutterShader();

    @NotNull
    private static ShaderInstance createScreenShutterShader() {
        try {
            return new ShaderInstance(minecraft.getResourceManager(), "screen_shutter", DefaultVertexFormat.POSITION_TEX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Style font = Style.EMPTY.withFont(new ResourceLocation(InteractiveEye.MODID, "rounded"));

    public static void render(PoseStack poseStack, float tickDelta) {
        if (!inspecting || inline_data.isEmpty()) {
            ani_progress = 0.0f;
            return;
        }
        ani_progress += tickDelta;
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        int hudsize = (minecraft.font.lineHeight + 2) * inline_data.size();
        int animated_hudsize = (int) (bezierCurveAnimation(Math.min(ani_progress/8.0f, 1.0f), 0, 0.75f, 1.0f, 1.0f) * hudsize);
        int x = width / 2 + 98;
        int y = height - animated_hudsize - 4;
        GuiComponent.fill(poseStack, x, height - 3, width - 2, height - 2, 0xff81e386);
        GuiComponent.fill(poseStack, x, y, width - 2, height - 3, minecraft.screen == null ? 0x40458a48 : 0xe00f2e11);
        GuiComponent.fill(poseStack, x, y - 1, width - 2, y, 0xff81e386);
        poseStack.translate(0.0, 0.0, 1000.0);
        if (animated_hudsize == hudsize) {
            BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().getBuilder();
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(bufferBuilder);
            for (int i = 0; i < inline_data.size(); i++) {
                Component styledDataLine = Component.literal(inline_data.get(i)).setStyle(font);
                minecraft.font.drawInBatch(styledDataLine, width / 2f + 100, height - (minecraft.font.lineHeight + 2) * (inline_data.size() - i) - 2, 0xc0ffffff, false, poseStack.last().pose(), bufferSource, false, 0, 0xF000F0);
            }
            bufferSource.endBatch();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(poseStack.last().pose(), x, y, 0.0f).color(0xffffffff).uv(0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), width - 2, y, 0.0f).color(0xffffffff).uv(1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), width - 2, height - 3, 0.0f).color(0xffffffff).uv(1.0f, 1.0f).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), x, height - 3, 0.0f).color(0xffffffff).uv(0.0f, 1.0f).endVertex();
            if (ani_progress < 14.0f) {
                RenderSystem.setShader(InspectionScreen::getScreenShutterShader);
                getScreenShutterShader().safeGetUniform("Intensity").set(ani_progress);
                BufferUploader.drawWithShader(bufferBuilder.end());
            } else {
                BufferUploader.draw(bufferBuilder.end());
            }
        }
    }

    @NotNull
    private static ShaderInstance getScreenShutterShader() {
        return SCREEN_SHUTTER_SHADER;
    }

    // Bezier curve formula provided by technobroken
    private static float bezierCurveAnimation(float t, float c0, float c1, float c2, float c3) {
        return (float) (Math.pow(t,3)*(c0+3.0f*c1-3.0f*c2+c3) + Math.pow(t,2)*(3.0f*c0-6.0f*c1+3.0f*c2)+t*(-3.0f*c0+3.0f*c1)+c0);
    }

    public static void inspect() {
        inline_data.clear();
        if (minecraft.level != null) {
            if (minecraft.screen == null) {
                inspect(minecraft.level);
            } else {
                inspect(minecraft.screen);
            }
        }
    }

    public static void inspect(Level level) {
        switch (minecraft.hitResult.getType()) {
            case ENTITY -> inspect(((EntityHitResult) minecraft.hitResult).getEntity());
            case BLOCK -> inspect(((BlockHitResult) minecraft.hitResult).getBlockPos(), level, (BlockHitResult)minecraft.hitResult);
            case MISS -> {
                long currentTime = level.getDayTime();
                int hour = ((int) Math.floor(currentTime * 0.001d) + 6) % 24;
                int min = (int) Math.floor(currentTime * 0.06d % 60d);
                inline_data.add("Dimension: " + Component.translatable(level.dimension().location().toLanguageKey()).getString());
                inline_data.add("Time: " + hour + ":" + String.format("%02d", min));
                inline_data.add("Weather: " + (level.isThundering() ? "thunder" : level.isRaining() ? "raining" : "clear"));
            }
        }
    }

    private static void inspect(Screen screen) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (screen instanceof EffectRenderingInventoryScreen<?>) {
                if (screen instanceof CreativeModeInventoryScreen creativeScreen) {
                    int selectedTabId = creativeScreen.getSelectedTab();
                    if (selectedTabId == CreativeModeTab.TAB_SEARCH.getId()) return;
                    inline_data.add("Current screen: Creative");
                    if (selectedTabId == CreativeModeTab.TAB_INVENTORY.getId()) {
                        inline_data.add("Current tab: Inventory");
                    } else {
                        CreativeModeTab selectedTab = null;
                        for (CreativeModeTab tab : CreativeModeTab.TABS) {
                            if (tab.getId() == selectedTabId) {
                                selectedTab = tab;
                                break;
                            }
                        }
                        inline_data.add("Current tab: " + selectedTab.getDisplayName().getString());
                    }
                } else {
                    inline_data.add("Current screen: Inventory");
                }
            } else {
                inline_data.add("Current screen: " + screen.getTitle().getString());
            }
            double xpos = minecraft.mouseHandler.xpos() * (double)minecraft.getWindow().getGuiScaledWidth() / (double)minecraft.getWindow().getScreenWidth();
            double ypos = minecraft.mouseHandler.ypos() * (double)minecraft.getWindow().getGuiScaledHeight() / (double)minecraft.getWindow().getScreenHeight();
            Slot hoveredSlot = containerScreen.findSlot(xpos, ypos);
            ItemStack carrying = containerScreen.getMenu().getCarried();
            if (hoveredSlot != null) {
                ItemStack hoveredStack = hoveredSlot.getItem();
                if (!hoveredStack.isEmpty()) {
                    inline_data.add("Item: " + hoveredStack.getItem().getDescription().getString());
                    inline_data.add("Type: " + hoveredStack.getItem().getItemCategory().getDisplayName().getString());
                    int maxStackSize = hoveredStack.getItem().getMaxStackSize();
                    if (maxStackSize > 1) {
                        inline_data.add(maxStackSize + " stackable");
                        if (!carrying.isEmpty()) {
                            if (hoveredSlot instanceof CreativeModeInventoryScreen.CustomCreativeSlot) {
                                inline_data.add("Destroys carried item");
                            } else if (!hoveredSlot.mayPlace(carrying)) {
                                inline_data.add("Cannot place inside");
                            } else if (ItemStack.isSameItemSameTags(carrying, hoveredStack) && hoveredStack.getCount() < maxStackSize && carrying.getCount() < maxStackSize) {
                                int stackleft = carrying.getCount() + hoveredStack.getCount() - maxStackSize;
                                inline_data.add(stackleft > 0 ? "Can stack, " + stackleft + " remains" : "Can stack");
                            } else {
                                inline_data.add("Cannot stack");
                            }
                        }
                    } else {
                        inline_data.add("Unstackable");
                        if (!carrying.isEmpty()) {
                            inline_data.add(hoveredSlot instanceof CreativeModeInventoryScreen.CustomCreativeSlot ? "Destroys carried item" : hoveredSlot.mayPlace(carrying) ? "Can place inside" : "Cannot place inside");
                        }
                    }
                    int damage = hoveredStack.getDamageValue();
                    if (damage != 0) {
                        int maxDamage = hoveredStack.getMaxDamage();
                        inline_data.add("Durability: " + (maxDamage - damage) + "/" + maxDamage);
                    }
                    if (hoveredStack.getItem().isEnchantable(hoveredStack)) {
                        if (hoveredStack.isEnchanted()) {
                            inline_data.add("Enchantment weight: " + hoveredStack.getBaseRepairCost());
                        } else {
                            inline_data.add("Enchantable");
                        }
                    }
                } else {
                    inline_data.add("Slot: Empty");
                    if (!carrying.isEmpty()) {
                        inline_data.add(hoveredSlot instanceof CreativeModeInventoryScreen.CustomCreativeSlot ? "Destroys carried item" : hoveredSlot.mayPlace(carrying) ? "Can place inside" : "Cannot place inside");
                    }
                }
            }
        }
    }


    @SuppressWarnings(value = "deprecation")
    private static void inspect(BlockPos pos, Level level, BlockHitResult hitResult) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        inline_data.add("Block: " + block.getName().getString());
        inline_data.add("Type: " + Objects.requireNonNullElse(StringMappings.materials.get(state.getMaterial()), "unknown"));
        ItemStack handItem = minecraft.player.getMainHandItem();
        if (handItem.is(Items.FILLED_MAP) || handItem.is(Items.MAP)) {
            inline_data.add("Color: " + Objects.requireNonNullElse(StringMappings.materialColors.get(state.getMapColor(level, pos)), "unknown"));
        }
        float breakspeed = 0.05f / block.getDestroyProgress(state, minecraft.player, level, pos);
        if (breakspeed == Float.POSITIVE_INFINITY) {
            inline_data.add("Unbreakable");
        } else if (breakspeed == 0.0f) {
            inline_data.add("Instabreakable");
        } else {
            inline_data.add("Break in: " + String.format("%.02f", breakspeed) + "sec");
        }
        if (!state.getValues().isEmpty()) {
            inline_data.add("");
            inline_data.add("Blockstate properties:");
            ImmutableSet<Map.Entry<Property<?>, Comparable<?>>> stateentries = state.getValues().entrySet();
            for (Map.Entry<Property<?>, Comparable<?>> entry : stateentries) {
                Property<?> property = entry.getKey();
                inline_data.add("  " + property.getName() + ": " + Util.getPropertyName(property, entry.getValue()));
            }
        }
        if (!handItem.isEmpty()) {
            if (handItem.getItem() instanceof BlockItem blockItem) {
                BlockPlaceContext placeContext = new BlockPlaceContext(minecraft.player, InteractionHand.MAIN_HAND, minecraft.player.getMainHandItem(), hitResult);
                inline_data.add("");
                BlockState placingState = blockItem.getPlacementState(placeContext);
                if (placingState != null && placeContext.canPlace()) {
                    inline_data.add((placeContext.getClickedPos().equals(pos) ? "Replaces: " : "Places: ") + placingState.getBlock().getName().getString());
                    if (!placingState.getValues().isEmpty()) {
                        ImmutableSet<Map.Entry<Property<?>, Comparable<?>>> stateentries = placingState.getValues().entrySet();
                        for (Map.Entry<Property<?>, Comparable<?>> entry : stateentries) {
                            Property<?> property = entry.getKey();
                            inline_data.add("  " + property.getName() + ": " + Util.getPropertyName(property, entry.getValue()));
                        }
                    }
                } else {
                    inline_data.add("Cannot be placed");
                }
            }
        }
        int redstoneSignal = level.getSignal(pos, hitResult.getDirection().getOpposite());
        if (redstoneSignal > 0) {
            inline_data.add("");
            inline_data.add("Redstone signal: " + redstoneSignal);
            if (state.isRedstoneConductor(level, pos) && level.getDirectSignalTo(pos) == redstoneSignal) {
                for(Direction direction : Direction.values()) {
                    BlockPos blockPos = pos.relative(direction);
                    if (level.getDirectSignal(blockPos, direction) == redstoneSignal) {
                        inline_data.add("Powered by: " + level.getBlockState(blockPos).getBlock().getName().getString());
                        break;
                    }
                }
            } else if (state.isSignalSource()) {
                inline_data.add("Power from source");
            } else {
                inline_data.add("Quazi powered");
            }
        }
    }

    public static void inspect(Entity entity) {
        inline_data.add("Name: " + entity.getType().getDescription().getString());
        if (entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.WITHER) {
            inline_data.add("Type: boss");
        } else if (entity instanceof LivingEntity livingEntity) {
            if (entity instanceof Enemy) {
                if (entity instanceof NeutralMob) {
                    inline_data.add("Type: neutral");
                } else {
                    inline_data.add("Type: hostile");
                }
            } else if (entity instanceof Npc) {
                inline_data.add("Type: human");
            } else if (entity instanceof Animal || entity instanceof AmbientCreature) {
                inline_data.add("Type: animal");
            } else if (entity instanceof NeutralMob) {
                inline_data.add("Type: protective");
            } else {
                inline_data.add("Type: passive");
            }
            inline_data.add("Health: " + (int)Math.ceil(livingEntity.getHealth()) + "/" + (int)livingEntity.getMaxHealth());

        } else {
            inline_data.add("Type: abstract");
        }
    }
}
