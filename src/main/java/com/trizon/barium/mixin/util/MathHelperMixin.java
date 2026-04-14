package com.trizon.barium.mixin.util;

import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Math Optimization Mixin
 * Replaces expensive sin/cos calculations with pre-calculated lookup tables
 * Priority: 500 (allows other mods to override)
 */
@Mixin(MathHelper.class)
public abstract class MathHelperMixin {
    
    private static final int SINE_TABLE_SIZE = 65536;
    private static final float[] SIN_TABLE = new float[SINE_TABLE_SIZE + 1];
    private static final float PI_OVER_2 = (float) (Math.PI / 2.0);
    private static final float TWO_PI = (float) (Math.PI * 2.0);
    
    static {
        // Pre-calculate sine lookup table for faster math operations
        for (int i = 0; i <= SINE_TABLE_SIZE; i++) {
            SIN_TABLE[i] = (float) Math.sin((double) i / SINE_TABLE_SIZE * PI_OVER_2);
        }
    }

    @Inject(
        method = "sin",
        at = @At("HEAD"),
        cancellable = true,
        priority = 500
    )
    private static void optimizedSin(float value, CallbackInfoReturnable<Float> cir) {
        // Use lookup table for sin calculation
        float normalized = ((value % TWO_PI) + TWO_PI) % TWO_PI;
        
        if (normalized < PI_OVER_2) {
            int index = (int) (normalized * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(SIN_TABLE[index]);
        } else if (normalized < Math.PI) {
            int index = (int) ((Math.PI - normalized) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(SIN_TABLE[index]);
        } else if (normalized < 3 * PI_OVER_2) {
            int index = (int) ((normalized - Math.PI) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(-SIN_TABLE[index]);
        } else {
            int index = (int) ((TWO_PI - normalized) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(-SIN_TABLE[index]);
        }
    }

    @Inject(
        method = "cos",
        at = @At("HEAD"),
        cancellable = true,
        priority = 500
    )
    private static void optimizedCos(float value, CallbackInfoReturnable<Float> cir) {
        // cos(x) = sin(x + π/2)
        float adjusted = value + PI_OVER_2;
        float normalized = ((adjusted % TWO_PI) + TWO_PI) % TWO_PI;
        
        if (normalized < PI_OVER_2) {
            int index = (int) (normalized * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(SIN_TABLE[index]);
        } else if (normalized < Math.PI) {
            int index = (int) ((Math.PI - normalized) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(SIN_TABLE[index]);
        } else if (normalized < 3 * PI_OVER_2) {
            int index = (int) ((normalized - Math.PI) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(-SIN_TABLE[index]);
        } else {
            int index = (int) ((TWO_PI - normalized) * SINE_TABLE_SIZE / PI_OVER_2);
            cir.setReturnValue(-SIN_TABLE[index]);
        }
    }
}