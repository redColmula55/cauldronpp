package rc55.mc.cauldronpp.mixin;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin {

    @Shadow protected abstract PotionContentsComponent getPotionContents();
    @Shadow @Final private static TrackedData<Integer> COLOR;

    //药箭颜色
    @Inject(at = @At("TAIL"), method = "initColor")
    public void initColor(CallbackInfo ci) {
        ArrowEntity self = ((ArrowEntity)(Object)this);
        if (this.getPotionContents().customColor().isPresent()) self.getDataTracker().set(COLOR, ColorHelper.Argb.fullAlpha(this.getPotionContents().getColor()));
    }
}
