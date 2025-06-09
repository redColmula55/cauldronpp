package rc55.mc.cauldronpp.item;

import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppPotionItem extends PotionItem {
    public CppPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE).group(ItemGroup.BREWING));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
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
            stacks.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 32767));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 16123));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 81621));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 55577));
        }
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? PotionUtil.getColor(stack) : -1;
    }
}
