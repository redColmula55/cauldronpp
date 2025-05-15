package rc55.mc.cauldronpp.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
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
        //getOrCreateTagBuilder(CauldronppItemTags.POTIONS).add(Items.POTION, CauldronppItems.CPP_POTION);
        getOrCreateTagBuilder(CauldronppItemTags.SPLASH_POTIONS).add(Items.SPLASH_POTION, CauldronppItems.CPP_SPLASH_POTION);
        getOrCreateTagBuilder(CauldronppItemTags.LINGERING_POTIONS).add(Items.LINGERING_POTION, CauldronppItems.CPP_LINGERING_POTION);
        getOrCreateTagBuilder(CauldronppItemTags.TIPPED_ARROWS).add(Items.TIPPED_ARROW, CauldronppItems.CPP_TIPPED_ARROW);
        getOrCreateTagBuilder(ItemTags.ARROWS).addTag(CauldronppItemTags.TIPPED_ARROWS);
    }
}
