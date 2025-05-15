package rc55.mc.cauldronpp.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppSplashPotionItem extends SplashPotionItem {
    public CppSplashPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE));
    }
    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionHelper.getPotionPrefixTranslationKey(1, stack.getOrCreateNbt().getInt("PotionData"));
    }
}
