package mc.mian.atg.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Shadow @org.jetbrains.annotations.Nullable public MultiPlayerGameMode gameMode;

    @Shadow @org.jetbrains.annotations.Nullable public HitResult hitResult;

    @Shadow public abstract @org.jetbrains.annotations.Nullable Entity getCameraEntity();

    @Nullable
    private HitResult rayTraceBlock(float partialTicks){
        assert player != null;
        Vec3 from = this.getCameraEntity().getEyePosition(partialTicks);
        Vec3 look = this.getCameraEntity().getViewVector(partialTicks);
        Vec3 to = from.add(look.x * gameMode.getPickRange(), look.y * gameMode.getPickRange(), look.z * gameMode.getPickRange());

        return player.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    @Nullable
    private EntityHitResult rayTraceEntity(float partialTicks) {
        assert player != null;
        Vec3 from = this.getCameraEntity().getEyePosition(partialTicks);
        Vec3 look = this.getCameraEntity().getViewVector(partialTicks);
        Vec3 to = from.add(look.x * gameMode.getPickRange(), look.y * gameMode.getPickRange(), look.z * gameMode.getPickRange());
        HitResult hitResult = rayTraceBlock(partialTicks);
        if (hitResult.getType() != HitResult.Type.MISS) {
            to = hitResult.getLocation();
        }

        return ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                from,
                to,
                new AABB(from, to),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR
                        .and(e -> e != null
                                && !e.isInvulnerable()
                                && e instanceof LivingEntity
                                && !getAllRidingEntities(player).contains(e)
                        )
        );
    }

    private static List<Entity> getAllRidingEntities(Player player) {
        List<Entity> ridingEntities = new ArrayList<>();
        Entity entity = player;
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
            ridingEntities.add(entity);
        }
        return ridingEntities;
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"))
    private void swapHitResult(CallbackInfoReturnable<Boolean> cir) {
        if(hitResult.getType() == HitResult.Type.BLOCK){
            BlockPos blockPos = BlockPos.containing(hitResult.getLocation());
            if(player.level().getBlockState(blockPos).getCollisionShape(player.level(), blockPos).isEmpty()){
                EntityHitResult entityHitResult = rayTraceEntity(1.0F);
                if(entityHitResult != null){
                    this.hitResult = entityHitResult;
                }
            }
        }
    }
}
