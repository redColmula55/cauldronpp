package rc55.mc.cauldronpp.blockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class CauldronppBlockEntityTypes {

    public static final BlockEntityType<CppCauldronBlockEntity> CAULDRON = register("cauldron", FabricBlockEntityTypeBuilder.create(CppCauldronBlockEntity::new, CauldronppBlocks.CPP_CAULDRON));

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Cauldronpp.MODID, id), builder.build());
    }
    public static void regBlockEntity() {
        Cauldronpp.LOGGER.info("Cauldron++ block entity registered.");
    }
}
