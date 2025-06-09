package rc55.mc.cauldronpp.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.PotionHelper;

import java.util.List;

public class CppLingeringPotionItem extends LingeringPotionItem {
    public CppLingeringPotionItem() {
        super(new Settings().recipeRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public Text getName(ItemStack stack) {
        int data = stack.getOrCreateNbt().getInt("PotionData");
        return stack.getOrCreateNbt().isEmpty() ? Text.translatable(this.getTranslationKey()+".default_name") :
                Text.translatable(this.getTranslationKey(), Text.translatable(PotionHelper.getPotionPrefixTranslationKey(data)));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        PotionUtil.buildTooltip(stack, tooltip, 1.0f);
    }
}
