package rc55.mc.cauldronpp.datagen.lootTable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class BlockLootTableDataGen extends FabricBlockLootTableProvider {
    public BlockLootTableDataGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockLootTables() {
        addDrop(CauldronppBlocks.CPP_CAULDRON);
    }
}
