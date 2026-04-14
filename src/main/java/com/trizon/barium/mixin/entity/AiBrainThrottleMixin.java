package com.trizon.barium.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Redirect;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class AiBrainThrottleMixin {

    @Redirect(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;updateGoals()V"),
        priority = 500
    )
    private void throttleGoalUpdate(MobEntity instance) {
        Entity entity = (Entity) (Object) instance;
        
        // Only update goals if player is within 32 blocks or if this is a special mob
        if (entity.getWorld() instanceof ServerWorld) {
            double distanceToNearestPlayer = ((MobEntity) (Object) this).distanceToNearestPlayer(32.0D);
            
            // If distance is -1.0D, player is within 32 blocks
            if (distanceToNearestPlayer == -1.0D || distanceToNearestPlayer <= 32.0D) {
                ((MobEntity) (Object) this).updateGoals();
            }
        }
    }

    @Redirect(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;updateAttackTarget()V"),
        priority = 500
    )
    private void throttleAttackTarget(MobEntity instance) {
        Entity entity = (Entity) (Object) instance;
        
        // Only update attack target if player is within 32 blocks
        if (entity.getWorld() instanceof ServerWorld) {
            double distanceToNearestPlayer = ((MobEntity) (Object) this).distanceToNearestPlayer(32.0D);
            
            if (distanceToNearestPlayer == -1.0D || distanceToNearestPlayer <= 32.0D) {
                ((MobEntity) (Object) this).updateAttackTarget();
            }
        }
    }
}