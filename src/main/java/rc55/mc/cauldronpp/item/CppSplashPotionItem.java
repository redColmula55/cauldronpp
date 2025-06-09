package rc55.mc.cauldronpp.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppSplashPotionItem extends SplashPotionItem {
    public CppSplashPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE).group(ItemGroup.BREWING));
    }

    @Override
    public Text getName(ItemStack stack) {
        int data = stack.getOrCreateNbt().getInt("PotionData");
        return stack.getOrCreateNbt().isEmpty() ? new TranslatableText(this.getTranslationKey()+".default_name") :
                new TranslatableText(this.getTranslationKey(), new TranslatableText(PotionHelper.getPotionPrefixTranslationKey(data)));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 32767));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 16123));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 81621));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 55577));
        }
    }
}
