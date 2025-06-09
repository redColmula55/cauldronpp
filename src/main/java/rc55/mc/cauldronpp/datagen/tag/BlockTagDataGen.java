package rc55.mc.cauldronpp.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.BlockTags;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class BlockTagDataGen extends FabricTagProvider.BlockTagProvider {


    public BlockTagDataGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(CauldronppBlocks.CPP_CAULDRON);
    }
}
