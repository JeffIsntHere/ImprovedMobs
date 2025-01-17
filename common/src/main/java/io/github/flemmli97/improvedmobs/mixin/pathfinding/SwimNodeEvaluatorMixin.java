package io.github.flemmli97.improvedmobs.mixin.pathfinding;

import io.github.flemmli97.improvedmobs.mixinhelper.INodeBreakable;
import io.github.flemmli97.improvedmobs.utils.PathFindingUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Duplicate of {@link FlyNodeMixin} but with SwimNodeEvaluator
 * See {@link <a href="https://github.com/SpongePowered/Mixin/issues/603">...</a>} as to why
 */
@Mixin(value = SwimNodeEvaluator.class)
public abstract class SwimNodeEvaluatorMixin extends NodeEvaluator {

    @Unique
    private final Object2BooleanMap<AABB> collisionBreakableCache = new Object2BooleanOpenHashMap<>();

    @Inject(method = "done", at = @At(value = "RETURN"))
    private void clearStuff(CallbackInfo info) {
        this.collisionBreakableCache.clear();
    }

    @Inject(method = "findAcceptedNode", at = @At(value = "HEAD"), cancellable = true)
    private void breakableNodes(int x, int y, int z, CallbackInfoReturnable<Node> info) {
        if (!((INodeBreakable) this).canBreakBlocks())
            return;
        Node node = PathFindingUtils.floatingNodeModifier(this.mob, this.currentContext.level(), x, y, z,
                aabb -> this.collisionBreakableCache.computeIfAbsent(aabb, object -> !PathFindingUtils.noCollision(this.currentContext.level(), this.mob, aabb)),
                p -> super.getNode(p.getX(), p.getY(), p.getZ()));
        if (node != null) {
            info.setReturnValue(node);
            info.cancel();
        }
    }
}