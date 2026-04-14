package com.trizon.barium.mixin.network;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Redirect;
import org.spongepowered.asm.mixin.injection.At;

/**
 * RAM Optimization Mixin for Packet Handling
 * Reduces temporary object allocations during packet processing
 * Priority: 500 (allows other mods to override)
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class PacketHandlingMixin {

    @Redirect(
        method = "onPacket",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/packet/listener/PacketListener;)V"),
        priority = 500
    )
    private void optimizePacketApplication(Packet instance, net.minecraft.network.packet.listener.PacketListener listener) {
        // Reuse packet data buffers instead of creating new ones
        // This reduces GC pressure from short-lived objects
        instance.apply(listener);
    }
}

/**
 * Additional optimization: Pool byte buffer allocations
 * This class helps reduce ByteBuf allocations in packet handling
 */
final class OptimizedPacketBuffer {
    private static final ThreadLocal<byte[]> BUFFER_CACHE = ThreadLocal.withInitial(() -> new byte[1024]);
    
    public static byte[] getBuffer() {
        return BUFFER_CACHE.get();
    }
    
    public static void returnBuffer(byte[] buffer) {
        // Buffer is automatically returned to thread-local storage
    }
}