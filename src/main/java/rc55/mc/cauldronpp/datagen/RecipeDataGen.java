package rc55.mc.cauldronpp.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.function.Consumer;

public class RecipeDataGen extends FabricRecipeProvider {
    public RecipeDataGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, CauldronppBlocks.CPP_CAULDRON).input(Items.CAULDRON).input(Blocks.BREWING_STAND).group("cppcauldron")
                .criterion(hasItem(Blocks.BREWING_STAND), conditionsFromItem(Blocks.BREWING_STAND)).offerTo(consumer, new Identifier(Cauldronpp.MODID, "cppcauldron"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CauldronppBlocks.CPP_CAULDRON).input('a', Items.IRON_INGOT).input('b', Blocks.BREWING_STAND)
                .pattern("a a").pattern("aba").pattern("aaa").group("cppcauldron")
                .criterion(hasItem(Blocks.BREWING_STAND), conditionsFromItem(Blocks.BREWING_STAND)).offerTo(consumer, new Identifier(Cauldronpp.MODID, "cppcauldron2"));
    }
}
