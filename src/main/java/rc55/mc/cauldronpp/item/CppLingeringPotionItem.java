package rc55.mc.cauldronpp.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import rc55.mc.cauldronpp.api.PotionHelper;

import java.util.List;

public class CppLingeringPotionItem extends LingeringPotionItem {
    public CppLingeringPotionItem() {
        super(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component == null ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(component.copyNbt().getInt("PotionData"))));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent != null) {
            potionContentsComponent.buildTooltip(tooltip::add, 1.0F, context.getUpdateTickRate());
        }
    }
}
