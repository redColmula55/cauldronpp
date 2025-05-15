package rc55.mc.cauldronpp.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppLingeringPotionItem extends LingeringPotionItem {
    public CppLingeringPotionItem() {
        super(new Settings());
    }
    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionHelper.getPotionPrefixTranslationKey(2, stack.getOrCreateNbt().getInt("PotionData"));
    }
}
