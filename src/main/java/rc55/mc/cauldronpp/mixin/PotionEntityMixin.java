package rc55.mc.cauldronpp.mixin;

import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.item.CauldronppItemTags;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {
    //滞留药水
    @Inject(at = @At("RETURN"), method = "isLingering", cancellable = true)
    public void isLingering(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((ThrownItemEntity)(Object)this).getStack().isIn(CauldronppItemTags.LINGERING_POTIONS));
    }
}
