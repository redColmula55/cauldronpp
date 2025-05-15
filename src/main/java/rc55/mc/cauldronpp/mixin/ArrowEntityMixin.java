package rc55.mc.cauldronpp.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.Collection;
import java.util.Set;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin {

    @Shadow protected abstract void initColor();
    @Shadow protected abstract void setColor(int color);
    @Final @Shadow private Set<StatusEffectInstance> effects;

    @Unique private ItemStack arrow;

    @Inject(at = @At("TAIL"), method = "initFromStack")
    public void initFromStack(ItemStack stack, CallbackInfo ci) {
        if (stack.isOf(CauldronppItems.CPP_TIPPED_ARROW)) {
            Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(stack);
            if (!collection.isEmpty()) {
                for (StatusEffectInstance statusEffectInstance : collection) {
                    this.effects.add(new StatusEffectInstance(statusEffectInstance));
                }
            }
            int i = ArrowEntity.getCustomPotionColor(stack);
            if (i == -1) {
                this.initColor();
            } else {
                this.setColor(i);
            }
        }
        this.arrow = stack;
    }

    @Inject(at = @At("HEAD"), method = "asItemStack", cancellable = true)
    public void asItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if (this.arrow.isOf(CauldronppItems.CPP_TIPPED_ARROW)) {
            ItemStack stack = new ItemStack(CauldronppItems.CPP_TIPPED_ARROW);
            stack.setNbt(this.arrow.getOrCreateNbt());
            cir.setReturnValue(stack);
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("tag", this.arrow.getOrCreateNbt());
        nbtCompound.putString("id", Registries.ITEM.getId(this.arrow.getItem()).toString());
        nbt.put("Item", nbtCompound);
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nbtCompound = nbt.getCompound("Item");
        Item item = Registries.ITEM.get(new Identifier(nbtCompound.getString("id")));
        this.arrow = new ItemStack(item);
        this.arrow.setNbt(nbtCompound.getCompound("tag"));
    }
}
