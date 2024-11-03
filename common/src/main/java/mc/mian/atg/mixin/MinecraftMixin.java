package mc.mian.atg.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public HitResult hitResult;

    // Retrieves all the vehicles that the player is riding
    @Unique
    private static List<Entity> attackThroughGrass$getAllVehicles(Player player) {
        List<Entity> allVehicles = new ArrayList<>();
        Entity vehicleEntity = player.getVehicle();
        while(vehicleEntity != null) {
            vehicleEntity = vehicleEntity.getVehicle();
            allVehicles.add(vehicleEntity);
        }
        return allVehicles;
    }

    // Swaps the hit result when attacking right before Minecraft checks it. This allows us to change hit result from a block to an entity if we find one.
    @Inject(method = "startAttack", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"))
    private void swapHitResult(CallbackInfoReturnable<Boolean> cir) {
        // No need to check if hitResult is null since the injected method already checks.
        if(hitResult.getType() == HitResult.Type.BLOCK){
            // The vec3 location of where player is looking.
            Vec3 hitResultLocation = hitResult.getLocation();
            // The pos of the block the player is looking at
            BlockPos blockPos = BlockPos.containing(hitResultLocation);
            // is the collision shape empty? Aka can we walk through it?
            if(player.level().getBlockState(blockPos).getCollisionShape(player.level(), blockPos).isEmpty()){
                // starting pos
                Vec3 start = player.getEyePosition();
                // direction that player is facing
                Vec3 direction = player.calculateViewVector(player.getXRot(), player.getYRot());
                // direction multiplied by entity interaction range which is then added to starting pos to get final entity pos
                Vec3 end = start.add(direction.scale(player.entityInteractionRange()));

                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                        // the level of the player
                        player.level(),
                        // we are the projectile lmao
                        player,
                        start,
                        end,
                        // bounding box
                        new AABB(start, end),
                        // skip creative & spectator users lol
                        EntitySelector.NO_CREATIVE_OR_SPECTATOR
                                .and(e -> e != null
                                        // can be hurt?
                                        && !e.isInvulnerable()
                                        // is alive and isn't like a boat
                                        && e instanceof LivingEntity
                                        // is not a vehicle that the player is in
                                        && !attackThroughGrass$getAllVehicles(player).contains(e)
                                )
                );
                if(entityHitResult != null){
                    // replaces the current hit result with the entity hit result
                    this.hitResult = entityHitResult;
                    // prevents block breaks
                    Minecraft.getInstance().missTime = 10;
                }
            }
        }
    }
}
