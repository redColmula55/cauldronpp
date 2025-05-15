package rc55.mc.cauldronpp.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppBlocks {

    public static final Block CPP_CAULDRON = register("cauldron", new CppCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON)));

    private static Block register(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(Cauldronpp.MODID, id), new BlockItem(block, new Item.Settings()));
        return registerNoItem(id, block);
    }
    private static Block registerNoItem(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Cauldronpp.MODID, id), block);
    }
    public static void regBlock() {
        Cauldronpp.LOGGER.info("Cauldron++ blocks registered.");
    }
}
