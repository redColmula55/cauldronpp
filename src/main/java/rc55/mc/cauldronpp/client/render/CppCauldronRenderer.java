package rc55.mc.cauldronpp.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;

import java.util.EnumMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CppCauldronRenderer implements BlockEntityRenderer<CppCauldronBlockEntity> {

    public static final SpriteIdentifier WATER_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(Cauldronpp.MODID, "block/cauldron_inner_water"));
    public static final SpriteIdentifier POTION_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(Cauldronpp.MODID, "block/cauldron_inner_potion"));
    public static final SpriteIdentifier LAVA_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(Cauldronpp.MODID, "block/cauldron_inner_lava"));
    public static final SpriteIdentifier SNOW_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(Cauldronpp.MODID, "block/cauldron_inner_snow"));

    public static final Map<CppCauldronLiquidType, SpriteIdentifier> SPRITES = Util.make(new EnumMap<>(CppCauldronLiquidType.class), map -> {
        map.put(CppCauldronLiquidType.WATER, WATER_SPRITE_ID);
        map.put(CppCauldronLiquidType.POTION, POTION_SPRITE_ID);
        map.put(CppCauldronLiquidType.LAVA, LAVA_SPRITE_ID);
        map.put(CppCauldronLiquidType.POWDER_SNOW, SNOW_SPRITE_ID);
        map.put(CppCauldronLiquidType.COLORED_WATER, WATER_SPRITE_ID);
    });

    private final ModelPart model;

    public CppCauldronRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = ctx.getLayerModelPart(CauldronppRenderer.CAULDRON_INNER);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData partData = data.getRoot();
        partData.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-6.0f,-0.1f,-6.0f,4.0f,0.0f,4.0f), ModelTransform.NONE);
        return TexturedModelData.of(data, 16, 16);
    }

    @Override
    public void render(CppCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world != null && !entity.isEmpty()) {
            BlockPos pos = entity.getPos();
            int lightUp = WorldRenderer.getLightmapCoordinates(world, pos.up());
            double height = entity.getFluidHeight();
            int color = entity.getLiquidRenderColor(world, pos);
            CppCauldronLiquidType liquidType = entity.getLiquidType();

            if (liquidType.shouldRenderColor()) {//药水，水
                this.render(matrices, vertexConsumers, SPRITES.getOrDefault(liquidType, WATER_SPRITE_ID), lightUp, overlay, color, height);
            } else {//岩浆，雪
                this.render(matrices, vertexConsumers, SPRITES.getOrDefault(liquidType, WATER_SPRITE_ID), lightUp, overlay, -1, height);
            }
        }
    }
    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpriteIdentifier sprite, int light, int overlay, int color, double height) {
        matrices.push();

        matrices.translate(1.25, height, 1.25);
        matrices.scale(3.0f, 1.0f, 3.0f);

        VertexConsumer consumer = sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);

        if (color == -1) {//没有颜色
            this.model.render(matrices, consumer, light, overlay);
        } else {
            float r = (color >> 16 & 0xff) / 255.0f;
            float g = (color >> 8 & 0xff) / 255.0f;
            float b = (color & 0xff) / 255.0f;
            this.model.render(matrices, consumer, light, overlay, color);
        }

        matrices.pop();
    }
}
