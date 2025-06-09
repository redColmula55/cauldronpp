package rc55.mc.cauldronpp.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterBottleItem extends PotionItem {
    public WaterBottleItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE).group(ItemGroup.BREWING));
    }

    @Override
    public ItemStack getDefaultStack() {
        return new ItemStack(this);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == ItemGroup.BREWING || group == ItemGroup.FOOD) stacks.add(this.getDefaultStack());
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? PotionUtil.getColor(Potions.WATER) : -1;
    }
}
