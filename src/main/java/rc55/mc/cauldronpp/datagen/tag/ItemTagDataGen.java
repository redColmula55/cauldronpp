package rc55.mc.cauldronpp.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import rc55.mc.cauldronpp.item.CauldronppItemTags;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagDataGen extends FabricTagProvider.ItemTagProvider {
    public ItemTagDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(CauldronppItemTags.SPLASH_POTIONS).add(Items.SPLASH_POTION, CauldronppItems.CPP_SPLASH_POTION);
        getOrCreateTagBuilder(CauldronppItemTags.LINGERING_POTIONS).add(Items.LINGERING_POTION, CauldronppItems.CPP_LINGERING_POTION);
        getOrCreateTagBuilder(CauldronppItemTags.TIPPED_ARROWS).add(Items.TIPPED_ARROW, CauldronppItems.CPP_TIPPED_ARROW);
        getOrCreateTagBuilder(ConventionalItemTags.POTIONS).add(CauldronppItems.CPP_POTION).addTag(CauldronppItemTags.SPLASH_POTIONS).addTag(CauldronppItemTags.LINGERING_POTIONS);
        getOrCreateTagBuilder(ItemTags.ARROWS).addTag(CauldronppItemTags.TIPPED_ARROWS);
        getOrCreateTagBuilder(CauldronppItemTags.POTION_MATERIALS)
                .add(Items.NETHER_WART, Items.SUGAR, Items.GHAST_TEAR,Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.BLAZE_POWDER, Items.MAGMA_CREAM,
                Items.RABBIT_FOOT,Items.GLOWSTONE_DUST, Items.REDSTONE, Items.GOLDEN_CARROT, Items.PHANTOM_MEMBRANE, Items.SLIME_BALL);
        getOrCreateTagBuilder(CauldronppItemTags.POTION_TYPE_MATERIALS).add(Items.GLISTERING_MELON_SLICE, Items.GUNPOWDER, Items.DRAGON_BREATH);
        getOrCreateTagBuilder(CauldronppItemTags.CAULDRON_BREWING_MATERIALS).addTag(CauldronppItemTags.POTION_MATERIALS).addTag(CauldronppItemTags.POTION_TYPE_MATERIALS);
    }
}
