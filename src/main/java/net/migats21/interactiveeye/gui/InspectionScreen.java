package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.migats21.interactiveeye.util.StringMappings;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class InspectionScreen implements HudRenderCallback {
    public static boolean inspecting;
    private static final List<String> inline_data = Lists.newArrayList();

    private static final Minecraft minecraft = Minecraft.getInstance();

    @Override
    public void onHudRender(PoseStack poseStack, float tickDelta) {
        if (minecraft.screen == null) {
            render(poseStack, tickDelta);
        }
    }
    public static void render(PoseStack poseStack, float tickDelta) {
        if (!inspecting || inline_data.isEmpty()) return;
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        for (int i=0;i<inline_data.size();i++) {
            String inlineDataLine = inline_data.get(i);
            minecraft.font.drawShadow(poseStack, inlineDataLine, scaledWidth / 2f + 95, scaledHeight - (minecraft.font.lineHeight + 2) * (inline_data.size() - i), 0xffffff);
        }
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
            case BLOCK -> inspect(((BlockHitResult) minecraft.hitResult).getBlockPos(), level);
            case MISS -> {
                long currentTime = level.getDayTime();
                int hour = ((int) Math.floor(currentTime * 0.001d) + 6) % 24;
                int min = (int) Math.floor(currentTime * 0.06d % 60d);
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
                    int damage = hoveredStack.getDamageValue();
                    if (damage != 0) {
                        int maxDamage = hoveredStack.getMaxDamage();
                        inline_data.add("Durability: " + (maxDamage - damage) + "/" + maxDamage);
                    }
                }
            }
        }
    }


    private static void inspect(BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        inline_data.add("Block: " + block.getName().getString());
        inline_data.add("Type: " + StringMappings.materials.get(state.getMaterial()));
        float breakspeed = 0.05f / block.getDestroyProgress(state, minecraft.player, level, pos);
        if (breakspeed == Float.POSITIVE_INFINITY) {
            inline_data.add("Unbreakable");
        } else if (breakspeed == 0.0f) {
            inline_data.add("Instabreakable");
        } else {
            inline_data.add("Break in: " + String.format("%.02f", breakspeed) + "sec");
        }
        if (state.getTags().count() > 0) {
            inline_data.add("");
            inline_data.add("Tags:");
            inline_data.addAll(state.getTags().limit(15).map(tag -> "  #" + tag.location().getPath()).toList());
        }
        if (!state.getValues().isEmpty()) {
            inline_data.add("");
            inline_data.add("Blockstate properties:");
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
                Property<?> property = entry.getKey();
                inline_data.add("  " + property.getName() + ": " + Util.getPropertyName(property, entry.getValue()));
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
