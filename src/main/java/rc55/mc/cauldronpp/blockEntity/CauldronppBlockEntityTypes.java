package rc55.mc.cauldronpp.blockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class CauldronppBlockEntityTypes {

    public static final BlockEntityType<CppCauldronBlockEntity> CAULDRON = register("cauldron", BlockEntityType.Builder.create(CppCauldronBlockEntity::new, CauldronppBlocks.CPP_CAULDRON));

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Cauldronpp.MODID, id), builder.build());
    }
    public static void regBlockEntity() {
        Cauldronpp.LOGGER.info("Cauldron++ block entity registered.");
    }
}
