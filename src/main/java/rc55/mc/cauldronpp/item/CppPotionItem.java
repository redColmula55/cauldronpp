package rc55.mc.cauldronpp.item;

import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.ActionResult;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppPotionItem extends PotionItem {
    public CppPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionHelper.getPotionPrefixTranslationKey(0, stack.getOrCreateNbt().getInt("PotionData"));
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? PotionUtil.getColor(stack) : -1;
    }
}
