package rc55.mc.cauldronpp.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;

@Environment(EnvType.CLIENT)
public class CppCauldronRenderer implements BlockEntityRenderer<CppCauldronBlockEntity> {

    public static final SpriteIdentifier POTION_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(Cauldronpp.MODID, "block/cauldron_inner_potion"));
    public static final SpriteIdentifier WATER_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(Cauldronpp.MODID, "block/cauldron_inner_water"));
    private final ModelPart potionModel;
    private final ModelPart waterModel;

    public CppCauldronRenderer(BlockEntityRendererFactory.Context ctx) {
        this.potionModel = ctx.getLayerModelPart(CauldronppRenderer.CAULDRON_INNER).getChild("inner_potion");
        this.waterModel = ctx.getLayerModelPart(CauldronppRenderer.CAULDRON_INNER).getChild("inner_water");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData partData = data.getRoot();
        partData.addChild("inner_potion", ModelPartBuilder.create().cuboid(-6.0f,-0.1f,-6.0f,4.0f,0.0f,4.0f).uv(0,0), ModelTransform.NONE);
        partData.addChild("inner_water", ModelPartBuilder.create().cuboid(-6.0f,-0.1f,-6.0f,4.0f,0.0f,4.0f).uv(0,0), ModelTransform.NONE);
        return TexturedModelData.of(data, 16, 16);
    }

    @Override
    public void render(CppCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        int amount = entity.getAmount();
        if (amount != 0) {
            World world = entity.getWorld();
            BlockPos pos = entity.getPos();
            int lightUp = WorldRenderer.getLightmapCoordinates(world, pos.up());
            int color;
            float r, g, b;

            matrices.translate(1.25, ((double) amount / 3), 1.25);
            matrices.scale(3.0f, 0.0f, 3.0f);
            //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            VertexConsumer consumer;
            if (entity.getPotionData() == 0) {
                color = BiomeColors.getWaterColor(world, pos);
                r = (color >> 16 & 0xff) / 255.0f;
                g = (color >> 8 & 0xff) / 255.0f;
                b = (color & 0xff) / 255.0f;
                consumer = WATER_SPRITE_ID.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
                this.waterModel.render(matrices, consumer, lightUp, overlay, r, g, b, -1);
            } else {
                color = (int) entity.getRenderData();
                r = (color >> 16 & 0xff) / 255.0f;
                g = (color >> 8 & 0xff) / 255.0f;
                b = (color & 0xff) / 255.0f;
                consumer = POTION_SPRITE_ID.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
                this.potionModel.render(matrices, consumer, lightUp, overlay, r, g, b, -1);
            }
        }
        matrices.pop();
    }
}
