package rc55.mc.cauldronpp.datagen.lootTable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableDataGen extends FabricBlockLootTableProvider {

    public BlockLootTableDataGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(CauldronppBlocks.CPP_CAULDRON);
    }
}
