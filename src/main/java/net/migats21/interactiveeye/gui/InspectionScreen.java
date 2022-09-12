package net.migats21.interactiveeye.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.migats21.interactiveeye.util.StringMappings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InspectionScreen implements HudRenderCallback {
    public static boolean inspecting;
    private static final List<String> inline_data = Lists.newArrayList();

    private static final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (client == null || !inspecting) return;
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        for (int i=0;i<inline_data.size();i++) {
            String inlineDataLine = inline_data.get(i);
            client.textRenderer.drawWithShadow(matrixStack, inlineDataLine, scaledWidth / 2f + 95, scaledHeight - (client.textRenderer.fontHeight + 2) * (inline_data.size() - i), 0xffffff);
        }
    }
    public static void inspect() {
        inline_data.clear();
        World level = client.player.world;
        switch (client.crosshairTarget.getType()) {
            case ENTITY -> inspect(((EntityHitResult) client.crosshairTarget).getEntity());
            case BLOCK -> inspect(((BlockHitResult) client.crosshairTarget).getBlockPos(), level);
            case MISS -> {
                long currentTime = level.getTimeOfDay();
                int hour = ((int) Math.floor(currentTime * 0.001d) + 6) % 24;
                int min = (int) Math.floor(currentTime * 0.06d % 60d);
                inline_data.add("Time: " + hour + ":" + String.format("%02d", min));
                inline_data.add("Weather: " + (level.isThundering() ? "thunder" : level.isRaining() ? "raining" : "clear"));
            }
        }
    }

    private static void inspect(BlockPos pos, World level) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        inline_data.add("Block: " + block.getName().getString());
        inline_data.add("Type: " + StringMappings.materials.get(state.getMaterial()));
        float breakspeed = 0.05f / block.calcBlockBreakingDelta(state, client.player, level, pos);
        if (breakspeed == Float.POSITIVE_INFINITY) {
            inline_data.add("Unbreakable");
        } else if (breakspeed == 0.0f) {
            inline_data.add("Instabreakable");
        } else {
            inline_data.add("Break in: " + String.format("%.02f", breakspeed) + "sec");
        }
        if (state.streamTags().count() > 0) {
            inline_data.add("");
            inline_data.add("Tags:");
            inline_data.addAll(state.streamTags().limit(15).map(tag -> "  #" + tag.id()).toList());
        }
        if (!state.getEntries().isEmpty()) {
            inline_data.add("");
            inline_data.add("Blockstate properties:");
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
                Property<?> property = entry.getKey();
                inline_data.add("  " + property.getName() + ": " + Util.getValueAsString(property, entry.getValue()));
            }
        }
    }

    public static void inspect(Entity entity) {
        inline_data.add("Name: " + entity.getType().getName().getString());
        String type;
        if (entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.WITHER) {
            type = "boss";
        } else if (entity instanceof LivingEntity livingEntity) {
            if (entity instanceof Monster) {
                if (entity instanceof Angerable) {
                    inline_data.add("Type: neutral");
                } else {
                    inline_data.add("Type: hostile");
                }
            } else if (entity instanceof Npc) {
                inline_data.add("Type: human");
            } else if (entity instanceof AnimalEntity || entity instanceof AmbientEntity) {
                inline_data.add("Type: animal");
            } else if (entity instanceof Angerable) {
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
