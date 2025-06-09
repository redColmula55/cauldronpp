package rc55.mc.cauldronpp.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {

    @Shadow
    private static boolean canDripThrough(BlockView world, BlockPos pos, BlockState state) {
        return false;
    }

    @Shadow
    private static Optional<BlockPos> searchInDirection(WorldAccess world, BlockPos pos, Direction.AxisDirection direction, BiPredicate<BlockPos, BlockState> continuePredicate, Predicate<BlockState> stopPredicate, int range) {
        return Optional.empty();
    }

    //滴水石锥滴水灌满炼药锅
    @Inject(at = @At("HEAD"), method = "getCauldronPos", cancellable = true)
    private static void getCauldronPos(World world, BlockPos pos, Fluid fluid, CallbackInfoReturnable<BlockPos> cir) {
        Predicate<BlockState> predicate = state -> state.isOf(CauldronppBlocks.CPP_CAULDRON);
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, state) -> canDripThrough(world, posx, state);
        searchInDirection(world, pos, Direction.DOWN.getDirection(), biPredicate, predicate, 11).ifPresent(cir::setReturnValue);
    }
}
