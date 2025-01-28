package mc.mian.atg.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"))
	public HitResult modifyPickedHitResult(HitResult original, @Local(ordinal = 0) Entity entity, @Local(ordinal = 0, argsOnly = true) float f) {
    // Ensure the hit result qualifies to be changed
		Minecraft minecraft = Minecraft.getInstance();
    Player player = minecraft.player;
    if (original != null && player != null && original.getType() == HitResult.Type.BLOCK) {
      // The vec3 location of where player is looking.
      Vec3 hitResultLocation = original.getLocation();
      // The pos of the block the player is looking at
      BlockPos blockPos = BlockPos.containing(hitResultLocation);
      // is the collision shape empty? Aka can we walk through it?
      if (player.level().getBlockState(blockPos).getCollisionShape(player.level(), blockPos).isEmpty()) {
        // starting pos
        Vec3 start = player.getEyePosition(1);
        // direction that player is facing
        Vec3 direction = player.getViewVector(1);
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
                    // can be picked
                    && e.isPickable()
                    // is alive and isn't like a boat
                    && e instanceof LivingEntity
                    // is not a vehicle that the player is in
                    && !attackThroughGrass$getAllVehicles(player).contains(e)
                )
            );
            
        if (entityHitResult != null) {
          // get distance to the target
          double enemyDistance = player.distanceTo(entityHitResult.getEntity());
          // try to pick a solid block between the player and the entity
				  HitResult newResult = attackThroughGrass$pickUncolliding(player, enemyDistance);
          // replaces the current hit result with the entity hit result provided there is no solid blocks between
				  if (newResult.getType() == HitResult.Type.MISS) {
					  original = entityHitResult;
            // make sure block does not break immediately after
            minecraft.missTime = 10;
          }
        }
      }
    }
		return original;
	}
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
  // Retrieves a hit result for a block that can be collided with
  @Unique
  private static HitResult attackThroughGrass$pickUncolliding(Entity entity, double reach) {
		Vec3 viewVector = entity.getViewVector(1);
		Vec3 pos = entity.getEyePosition(1);
		Vec3 endPos = pos.add(viewVector.scale(reach));
		return entity.level().clip(new ClipContext(pos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
	}
}
