package rc55.mc.cauldronpp.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.text.Text;
import rc55.mc.cauldronpp.api.PotionHelper;

public class CppSplashPotionItem extends SplashPotionItem {
    public CppSplashPotionItem() {
        super(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component == null ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(component.copyNbt().getInt("PotionData"))));
    }
}
