package rc55.mc.cauldronpp.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.item.CauldronppItems;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin {
    @Shadow protected abstract ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack);

    //空瓶右键装水
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/GlassBottleItem;fill(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        cir.setReturnValue(TypedActionResult.success(fill(user.getStackInHand(hand), user, new ItemStack(CauldronppItems.WATER_BOTTLE))));
    }
}
