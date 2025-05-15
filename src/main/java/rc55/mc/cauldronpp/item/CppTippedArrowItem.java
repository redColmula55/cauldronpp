package rc55.mc.cauldronpp.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppTippedArrowItem extends TippedArrowItem {
    public CppTippedArrowItem() {
        super(new Settings());
    }
    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionHelper.getPotionPrefixTranslationKey(3, stack.getOrCreateNbt().getInt("PotionData"));
    }
}
