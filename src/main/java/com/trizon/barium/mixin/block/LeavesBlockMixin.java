package com.trizon.barium.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Redirect;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {

    public LeavesBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(
        method = "randomTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LeavesBlock;tryDecay(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z"),
        priority = 500
    )
    private boolean fastLeafDecay(LeavesBlock instance, BlockState state, World world, BlockPos pos) {
        // Lightweight leaf decay: check distance to log blocks more frequently
        if (hasNearbyLog(world, pos, 6)) { // Increased from default 4 to 6 for faster decay
            return true;
        }
        
        // Schedule decay for next tick if far from logs
        world.scheduleBlockTick(pos, this, 1); // Reduce delay from typical 2+ to 1
        return false;
    }

    private boolean hasNearbyLog(World world, BlockPos pos, int range) {
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockState blockState = world.getBlockState(pos.add(x, y, z));
                    if (blockState.contains(LeavesBlock.DISTANCE) && blockState.get(LeavesBlock.DISTANCE) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}