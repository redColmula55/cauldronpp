package rc55.mc.cauldronpp.item;

import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
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
    public Text getName(ItemStack stack) {
        int data = stack.getOrCreateNbt().getInt("PotionData");
        return stack.getOrCreateNbt().isEmpty() ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(data)));
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? PotionUtil.getColor(stack) : -1;
    }
}
