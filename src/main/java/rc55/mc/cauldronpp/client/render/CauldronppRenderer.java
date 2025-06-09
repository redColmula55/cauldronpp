package rc55.mc.cauldronpp.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.item.CppPotionItem;
import rc55.mc.cauldronpp.item.WaterBottleItem;

public class CauldronppRenderer {
    public static void regRenderer() {
        regColoredItem();
        regBlockEntityRenderer();
        Cauldronpp.LOGGER.info("Cauldron++ renderer added.");
    }

    public static EntityModelLayer CAULDRON_INNER = new EntityModelLayer(new Identifier(Cauldronpp.MODID, "cauldron"), "main");

    private static void regColoredItem() {
        ColorProviderRegistry.ITEM.register(CppPotionItem::getColor, CauldronppItems.CPP_POTION, CauldronppItems.CPP_SPLASH_POTION,
                CauldronppItems.CPP_LINGERING_POTION, CauldronppItems.CPP_TIPPED_ARROW);
        ColorProviderRegistry.ITEM.register(WaterBottleItem::getColor, CauldronppItems.WATER_BOTTLE);
    }
    private static void regBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(CauldronppBlockEntityTypes.CAULDRON, CppCauldronRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAULDRON_INNER, CppCauldronRenderer::getTexturedModelData);
    }
}
