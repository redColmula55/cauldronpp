package rc55.mc.cauldronpp.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ColorHelper;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppPotionItem extends PotionItem {
    public CppPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component == null ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(component.copyNbt().getInt("PotionData"))));
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ColorHelper.Argb.fullAlpha(stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getColor()) : -1;
    }
}
