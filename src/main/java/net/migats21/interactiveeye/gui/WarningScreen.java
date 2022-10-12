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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarningScreen extends GlobalHudScreen {

    private static final ResourceLocation WARNING_TEXTURE = new ResourceLocation(InteractiveEye.MODID, "textures/gui/warning_sign.png");
    private static final DamageSource POISON_DAMAGE_SOURCE = new DamageSource("magic.poison").bypassArmor().setMagic();
    private float healthDanger;

    private DamageSource warningDeathCause;

    public WarningScreen() {
        ClientTickEvents.START_WORLD_TICK.register(this::tick);
    }
    @Override
    @Deprecated
    protected void show() {
        throw new IllegalCallerException("Method show cannot be called on WarningScreen");
    }
    @Override
    protected void render(PoseStack poseStack, float deltaFrameTime, int width, int height) {
        if (warningDeathCause != null) {
            int x = 4;
            int y = height/2 - 32;
            int hudWidth = 160;
            int hudHeight = 64;
            renderBackground(poseStack, x, y, hudWidth, hudHeight);
            RenderSystem.setShaderTexture(0, WARNING_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.75f);
            poseStack.translate(0.0, 0.0, 1000.0);
            blit(poseStack, x + 1, y + 1, 0, 0, 0, 64, 64, 64, 64);
            poseStack.pushPose();
            poseStack.scale(2.0f, 2.0f, 1.0f);
            Component styledData = Component.literal("WARNING!").withStyle(font);
            minecraft.font.draw(poseStack, styledData, x/2.0f + 28, y/2.0f + 4, 0xc0ffcc00);
            poseStack.popPose();
            styledData = getLocalizedWarningMessage().withStyle(font.withColor(0xc0ffffff));
            minecraft.font.drawWordWrap(styledData, x + 64, y + 28, 92, 3);
        }
    }

    @NotNull
    private MutableComponent getLocalizedWarningMessage() {
        return warningDeathCause.getDirectEntity() == null ? Component.translatable("warning.attack." + warningDeathCause.msgId) : Component.translatable("warning.attack." + warningDeathCause.msgId, warningDeathCause.getDirectEntity().getDisplayName());
    }

    protected void setAlert(float f) {
        this.healthDanger = f;
    }

    public void tick(ClientLevel level) {
        if (minecraft.player == null) return;
        DamageSource cause = getWarningDeathCause(level, minecraft.player);
        if (cause == null) {
            warningDeathCause = null;
            return;
        }
        if (warningDeathCause != null && Objects.equals(cause.msgId, warningDeathCause.msgId) && cause.getDirectEntity() == warningDeathCause.getDirectEntity() && cause.getEntity() == warningDeathCause.getEntity()) {
            return;
        }
        warningDeathCause = cause;
        minecraft.getNarrator().sayNow(Component.literal("Warning. ").append(getLocalizedWarningMessage()));
    }

    @SuppressWarnings("Deprication")
    private DamageSource getWarningDeathCause(Level level, Player player) {
        if (!player.isAlive()) {
            healthDanger = 0.0f;
            return null;
        }
        if (healthDanger > 0.0f && healthDanger >= player.getHealth()) {
            return DamageSource.GENERIC;
        }
        if (player.isInWall()) {
            return DamageSource.IN_WALL;
        }
        if (player.fallDistance > 0) {
            if (player.getY() < level.getMinBuildHeight()) {
                return DamageSource.OUT_OF_WORLD;
            }
            int i = player.calculateFallDamage(player.fallDistance, 1.0f);
            if (i > player.getHealth() && i > 12.0f) {
                return DamageSource.FALL;
            }
        }
        if (player.isOnFire() && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            if (player.isInLava()) {
                return DamageSource.LAVA;
            }
            return DamageSource.ON_FIRE;
        }
        if (player.hasEffect(MobEffects.WITHER)) {
            return DamageSource.WITHER;
        }
        if (player.hasEffect(MobEffects.POISON)) {
            return POISON_DAMAGE_SOURCE;
        }
        if (level.getBlockState(player.getOnPosLegacy()).is(Blocks.MAGMA_BLOCK) && !player.isSteppingCarefully() && !EnchantmentHelper.hasFrostWalker(player)) {
            return DamageSource.HOT_FLOOR;
        }
        List<BlockState> states = level.getBlockStates(player.getBoundingBox()).toList();
        for (BlockState state : states) {
            if (state.getBlock() instanceof CactusBlock) { // Should also work on modded cacti
                return DamageSource.CACTUS;
            }
            if (state.getBlock() instanceof SweetBerryBushBlock) {
                return DamageSource.SWEET_BERRY_BUSH;
            }
            if (state.getBlock() instanceof CampfireBlock) {
                return DamageSource.HOT_FLOOR;
            }
        }
        if (player.isFullyFrozen() && player.canFreeze()) {
            return DamageSource.FREEZE;
        }
        List<Entity> entities = level.getEntities(null, player.getBoundingBox().inflate(64.0));
        List<LivingEntity> enemies = new ArrayList<>();
        for(Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof NeutralMob neutralMob) {
                    if (neutralMob.isAngry()) {
                        // TODO: Make neutral mobs anger system use the represented attributes
                        return DamageSource.mobAttack(livingEntity);
                    }
                    continue;
                }
                if (livingEntity instanceof Enemy && entity.distanceTo(player) < 5.0f) {
                    if (livingEntity instanceof Creeper creeper && (creeper.getSwellDir() > 0 || creeper.isIgnited()) || livingEntity instanceof WitherBoss witherBoss && witherBoss.getInvulnerableTicks() > 100) {
                        return DamageSource.explosion(livingEntity);
                    }
                    enemies.add(livingEntity);
                    continue;
                }
            }
            if (entity instanceof PrimedTnt || entity instanceof MinecartTNT && level.getBlockState(new BlockPos(Mth.floor(entity.getX()), (Mth.floor(entity.getY())), Mth.floor(entity.getZ()))).is(Blocks.POWERED_RAIL) && entity.distanceTo(player) < 8) {
                return DamageSource.explosion((LivingEntity) null);
            }
            if (entity instanceof Projectile projectile && isDamageableProjectile(projectile, player)) {
                if (entity instanceof ShulkerBullet || entity instanceof ThrownPotion) {
                    if (entity.distanceTo(player) < 5.0f) {
                        if (projectile.getOwner() instanceof LivingEntity livingEntity) {
                            return new IndirectEntityDamageSource("projectile", entity, livingEntity);
                        } else {
                            return new IndirectEntityDamageSource("projectile", entity, null);
                        }
                    }
                } else {
                    double d = player.getX() - entity.getX();
                    double e = player.getZ() - entity.getZ();
                    // TODO: Use a mixin on the arrow movement instead of using DeltaMovement
                    Vec3 deltaMovement = entity.getDeltaMovement();
                    double f = 0;
                    if (!(entity instanceof AbstractHurtingProjectile)) f = Math.sqrt(d*d+e*e) / Math.sqrt(deltaMovement.x*deltaMovement.x+deltaMovement.z*deltaMovement.z); //* Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z);
                    Vec3 checkingMovement = deltaMovement.multiply(64.0, 64.0, 64.0).add(0.0, f * -1.42, 0.0);
                    HitResult hitResult = level.clip(new ClipContext(entity.position(), entity.position().add(checkingMovement), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
                    if (hitResult.getType() != HitResult.Type.MISS) {
                        Vec3 hitPos = hitResult.getLocation();
                        hitResult = Objects.requireNonNullElse(ProjectileUtil.getEntityHitResult(level, entity, entity.position(), hitPos, entity.getBoundingBox().expandTowards(deltaMovement).inflate(64.0), projectile::canHitEntity), hitResult);
                        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() == player) {
                            if (projectile.getOwner() instanceof LivingEntity livingEntity) {
                                return new IndirectEntityDamageSource("projectile", entity, livingEntity);
                            } else {
                                return new IndirectEntityDamageSource("projectile", entity, null);
                            }
                        }
                    }
                }
            }
        }
        if (player.getAirSupply() <= 0) {
            if (player.getMaxHealth() - player.getHealth() > EnchantmentHelper.getRespiration(player) * 2) {
                return DamageSource.DROWN;
            }
        }
        if (player.getFoodData().getFoodLevel() <= 6.0f) {
            return DamageSource.STARVE;
        }
        if (!enemies.isEmpty()) {
            return DamageSource.mobAttack(enemies.get(0));
        }
        return null;
    }

    private static boolean isDamageableProjectile(Projectile entity, Player player) {
        if (entity instanceof ThrownPotion potion) {
            return PotionUtils.getMobEffects(potion.getItem()).stream().anyMatch((effect) -> effect.getEffect() == MobEffects.HARM);
        }
        if (entity instanceof LlamaSpit) {
            return player.getHealth() < 4.0f;
        }
        if (entity instanceof ThrowableProjectile || entity instanceof FishingHook) {
            return false;
        }
        if (entity instanceof FireworkRocketEntity rocket) {
            CompoundTag compoundTag = rocket.getItem().getTagElement("Fireworks");
            ListTag listTag = compoundTag == null ? null : compoundTag.getList("Explosions", 10);
            return listTag != null && !listTag.isEmpty();
        }
        if (entity instanceof AbstractArrow arrow && arrow.getPierceLevel() <= 0) {
            return !player.isBlocking();
        }
        return true;
    }
}
