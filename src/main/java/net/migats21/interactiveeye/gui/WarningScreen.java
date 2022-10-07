package net.migats21.interactiveeye.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.migats21.interactiveeye.InteractiveEye;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class WarningScreen extends GlobalHudScreen {

    private static final ResourceLocation WARNING_TEXTURE = new ResourceLocation(InteractiveEye.MODID, "textures/gui/warning_sign.png");
    private float healthDanger;

    private DamageSource cause;

    public WarningScreen() {
        ClientTickEvents.START_WORLD_TICK.register(this::tick);
    }
    @Override
    @Deprecated
    protected void show() {
        throw new IllegalCallerException("Medhod show cannot be called on WarningScreen");
    }
    @Override
    protected void render(PoseStack poseStack, float deltaFrameTime, int width, int height) {
        if (cause != null) {
            int x = 4;
            int y = height/2 - 32;
            int hudWidth = 160;
            int hudHeight = 64;
            renderBackground(poseStack, x, y, hudWidth, hudHeight);
            RenderSystem.setShaderTexture(0, WARNING_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.75f);
            poseStack.translate(0.0, 0.0, 1000.0);
            poseStack.pushPose();
            poseStack.scale(0.25f, 0.25f, 1.0f);
            blit(poseStack, x*4 + 4, y*4 + 4, 0, 0, 256, 256);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.scale(2.0f, 2.0f, 1.0f);
            Component styledData = Component.literal("WARNING!").withStyle(font);
            minecraft.font.draw(poseStack, styledData, x/2.0f + 28, y/2.0f + 4, 0xc0ffcc00);
            poseStack.popPose();
            styledData = (cause.getDirectEntity() == null ? Component.translatable("warning.attack." + cause.msgId) : Component.translatable("warning.attack." + cause.msgId, cause.getDirectEntity().getDisplayName())).withStyle(font.withColor(0xc0ffffff));
            minecraft.font.drawWordWrap(styledData, x + 64, y + 28, 92, 3);
        }
    }

    protected void setAlert(float f) {
        this.healthDanger = f;
    }

    public void tick(ClientLevel level) {
        cause = null;
        if (minecraft.player == null || !minecraft.player.isAlive()) return;
        if (healthDanger > 0.0f && minecraft.player.getHealth() < healthDanger) {
            cause = minecraft.player.getLastDamageSource();
            return;
        }
        if (minecraft.player.fallDistance > 0) {
            if (minecraft.player.getY() < level.getMinBuildHeight()) {
                cause = DamageSource.OUT_OF_WORLD;
                return;
            }
            int i = minecraft.player.calculateFallDamage(minecraft.player.fallDistance, 1.0f);
            if (i > minecraft.player.getHealth() && i > 6.0f) {
                cause = DamageSource.FALL;
                return;
            }
        }
        if (minecraft.player.isOnFire() && !minecraft.player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            if (minecraft.player.isInLava()) {
                cause = DamageSource.LAVA;
                return;
            }
            cause = DamageSource.ON_FIRE;
            return;
        }
        List<Entity> entities = level.getEntities(null, minecraft.player.getBoundingBox().inflate(64.0));
        for(Entity entity : entities) {
            if (entity instanceof Creeper creeper && (creeper.getSwellDir() > 0 || creeper.isIgnited()) && entity.distanceTo(minecraft.player) < 5.0f) {
                cause = DamageSource.explosion(creeper);
                return;
            }
            if (entity instanceof PrimedTnt || entity instanceof MinecartTNT && level.getBlockState(new BlockPos(Mth.floor(entity.getX()), (Mth.floor(entity.getY())) - 1, Mth.floor(entity.getZ()))).is(Blocks.POWERED_RAIL)) {
                cause = DamageSource.explosion((LivingEntity) null);
                return;
            }
            if (entity instanceof Projectile projectile && isDamagableProjectile(projectile)) {
                if (entity instanceof ShulkerBullet || entity instanceof ThrownPotion) {
                    if (entity.distanceTo(minecraft.player) < 5.0f) {
                        if (projectile.getOwner() instanceof LivingEntity livingEntity) {
                            cause = new IndirectEntityDamageSource("projectile", entity, livingEntity);
                        } else {
                            cause = new IndirectEntityDamageSource("projectile", entity, null);
                        }
                        return;
                    }
                } else {
                    double d = minecraft.player.getX() - entity.getX();
                    double e = minecraft.player.getZ() - entity.getZ();
                    Vec3 deltaMovement = entity.getDeltaMovement();
                    double f = Math.sqrt(d*d+e*e) / Math.sqrt(deltaMovement.x*deltaMovement.x+deltaMovement.z*deltaMovement.z); //* Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z);
                    Vec3 checkingMovement = deltaMovement.multiply(64.0, 64.0, 64.0).add(0.0, f * -1.42, 0.0);
                    HitResult hitResult = level.clip(new ClipContext(entity.position(), entity.position().add(checkingMovement), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
                    if (hitResult.getType() != HitResult.Type.MISS) {
                        Vec3 hitPos = hitResult.getLocation();
                        hitResult = Objects.requireNonNullElse(ProjectileUtil.getEntityHitResult(level, entity, entity.position(), hitPos, entity.getBoundingBox().expandTowards(deltaMovement).inflate(64.0), projectile::canHitEntity), hitResult);
                        if (hitResult.getType() == HitResult.Type.ENTITY) {
                            if (projectile.getOwner() instanceof LivingEntity livingEntity) {
                                cause = new IndirectEntityDamageSource("projectile", entity, livingEntity);
                            } else {
                                cause = new IndirectEntityDamageSource("projectile", entity, null);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private static boolean isDamagableProjectile(Projectile entity) {
        if (entity instanceof ThrownPotion potion) {
            return PotionUtils.getMobEffects(potion.getItem()).stream().anyMatch((effect) -> effect.getEffect() == MobEffects.HARM);
        }
        if (entity instanceof LlamaSpit) {
            return minecraft.player.getHealth() < 4.0f;
        }
        if (entity instanceof ThrowableProjectile || entity instanceof FishingHook) {
            return false;
        }
        if (entity instanceof FireworkRocketEntity rocket) {
            CompoundTag compoundTag = rocket.getItem().getTagElement("Fireworks");
            ListTag listTag = compoundTag == null ? null : compoundTag.getList("Explosions", 10);
            return listTag != null && !listTag.isEmpty();
        }
        return true;
    }
}
