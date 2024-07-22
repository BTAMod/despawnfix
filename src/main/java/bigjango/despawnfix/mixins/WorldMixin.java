package bigjango.despawnfix.mixins;

import net.minecraft.core.world.World;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {
    @Shadow
    public List<EntityPlayer> players;

    // Comment from BTA:
    /**
     * @param radius Radius around the point to search for a player, a radius less than 0 denotes searching for the closest player with 0 regard for radius
     */
    // I like that "with 0 regard" bit, kinda funny. Anywho, this broke because less than 0 alway returned NULL, phooey.
    @Inject(method = "getClosestPlayer", at = @At("HEAD"), cancellable = true)
    void getClosestPlayer(double x, double y, double z, double radius, CallbackInfoReturnable<EntityPlayer> cir) {
        // Don't change these names, they are the ones used in Offical® BTA© Code™
        double closestDistance = Double.POSITIVE_INFINITY;
        EntityPlayer entityplayer = null;
        if (radius < 0) {
            for (EntityPlayer entityPlayer1 : players) {
                double currentDistance = entityPlayer1.distanceToSqr(x, y, z);
                if ((currentDistance < closestDistance)) {
                    closestDistance = currentDistance;
                    entityplayer = entityPlayer1;
                }
            }
        } else {
            double rSquared = radius * radius;
            for (EntityPlayer entityPlayer1 : players) {
                double currentDistance = entityPlayer1.distanceToSqr(x, y, z);
                if (currentDistance < rSquared && (currentDistance < closestDistance)) {
                    closestDistance = currentDistance;
                    entityplayer = entityPlayer1;
                }
            }
        }
        cir.setReturnValue(entityplayer);
        //return entityplayer;
    }
}
