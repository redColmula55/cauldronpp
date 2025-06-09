package rc55.mc.cauldronpp.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.text.Text;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppSplashPotionItem extends SplashPotionItem {
    public CppSplashPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public Text getName(ItemStack stack) {
        int data = stack.getOrCreateNbt().getInt("PotionData");
        return stack.getOrCreateNbt().isEmpty() ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(data)));
    }
}
