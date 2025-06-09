package rc55.mc.cauldronpp.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppBlocks {

    public static final Block CPP_CAULDRON = register("cauldron", new CppCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON)
            .luminance(state -> state.get(CppCauldronBlock.EMITS_LIGHT) ? 15 : 0)), new Item.Settings().group(ItemGroup.BREWING));

    private static Block register(String id, Block block, Item.Settings settings) {
        Registry.register(Registry.ITEM, new Identifier(Cauldronpp.MODID, id), new BlockItem(block, settings));
        return registerNoItem(id, block);
    }
    private static Block registerNoItem(String id, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(Cauldronpp.MODID, id), block);
    }
    public static void regBlock() {
        Cauldronpp.LOGGER.info("Cauldron++ blocks registered.");
    }
}
