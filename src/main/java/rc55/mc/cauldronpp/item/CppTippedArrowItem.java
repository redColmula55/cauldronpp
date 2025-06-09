package rc55.mc.cauldronpp.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.PotionHelper;

import java.util.List;

public class CppTippedArrowItem extends TippedArrowItem {
    public CppTippedArrowItem() {
        super(new Settings().group(ItemGroup.BREWING));
    }

    @Override
    public Text getName(ItemStack stack) {
        int data = stack.getOrCreateNbt().getInt("PotionData");
        return stack.getOrCreateNbt().isEmpty() ? new TranslatableText(this.getTranslationKey()+".default_name") :
                new TranslatableText(this.getTranslationKey(), new TranslatableText(PotionHelper.getPotionPrefixTranslationKey(data)));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        PotionUtil.buildTooltip(stack, tooltip, 1.0f);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 32767));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 16123));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 81621));
            stacks.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 55577));
        }
    }
}
