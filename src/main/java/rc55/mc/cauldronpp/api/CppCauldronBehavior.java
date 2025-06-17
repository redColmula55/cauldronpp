package rc55.mc.cauldronpp.api;

import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import rc55.mc.cauldronpp.block.CppCauldronBlock;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface CppCauldronBehavior {
    //交互
    ActionResult interact(World world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, PlayerEntity player, Hand hand, ItemStack stack);
    //空交互（不进行任何操作，直接返回ActionResult.PASS）
    CppCauldronBehavior EMPTY = (world, pos, state, blockEntity, player, hand, stack) -> ActionResult.PASS;
    //常量
    int BOTTLE_LEVEL = 1;//1瓶
    int BUCKET_LEVEL = 3;//1桶（3瓶）
    int MAX_LEVEL = 3;//最大容量（1桶）
    //清洗潜影盒
    CppCauldronBehavior CLEAN_SHULKER_BOX = (world, pos, state, cauldron, player, hand, stack) -> {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS;
        } else if (!cauldron.canDecrease(1)) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Blocks.SHULKER_BOX, 1);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, itemStack, false));
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                cauldron.decreaseAmount(1);
                update(world, pos, state, cauldron, player);
            }
            return ActionResult.success(world.isClient);
        }
    };
    //清洗旗帜
    CppCauldronBehavior CLEAN_BANNER = (world, pos, state, cauldron, player, hand, stack) -> {
        BannerPatternsComponent bannerPatternsComponent = stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        if (bannerPatternsComponent.layers().isEmpty()) {
            return ActionResult.PASS;
        } else if (!cauldron.canDecrease(1)) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = stack.copyWithCount(1);
                itemStack.set(DataComponentTypes.BANNER_PATTERNS, bannerPatternsComponent.withoutTopLayer());
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, itemStack, false));
                cauldron.decreaseAmount(1);
                update(world, pos, state, cauldron, player);
            }

            return ActionResult.success(world.isClient);
        }
    };
    //清洗，染色可染色的皮革盔甲
    CppCauldronBehavior DYEABLE_ITEM_BEHAVIOR = (world, pos, state, cauldron, player, hand, stack) -> {
        if (!(stack.isIn(ItemTags.DYEABLE))) {
            return ActionResult.PASS;
        } else if (cauldron.canDecrease(1)){
            if (cauldron.getLiquidType() == CppCauldronLiquidType.WATER) {//清洗
                if (!stack.contains(DataComponentTypes.DYED_COLOR)) return ActionResult.CONSUME;
                if (!world.isClient) {
                    stack.remove(DataComponentTypes.DYED_COLOR);
                    cauldron.decreaseAmount(1);
                    update(world, pos, state, cauldron, player);
                }
            } else if (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER) {//染色
                if (!world.isClient) {
                    if (stack.contains(DataComponentTypes.DYED_COLOR)) {
                        //dyeableItem.setColor(stack, getDyedColor(dyeableItem.getColor(stack), cauldron.getLiquidData()));
                        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(getDyedColor(stack.get(DataComponentTypes.DYED_COLOR).rgb(), cauldron.getLiquidData()), true));
                    } else {
                        //dyeableItem.setColor(stack, cauldron.getLiquidData());
                        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(cauldron.getLiquidData(), true));
                    }
                    player.setStackInHand(hand, stack);
                    cauldron.decreaseAmount(1);
                    update(world, pos, state, cauldron, player);
                }
            }

            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    };
    //染色
    CppCauldronBehavior DYE_ITEM_BEHAVIOR = (world, pos, state, cauldron, player, hand, stack) -> {
        if (!cauldron.isEmpty() && (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER || cauldron.getLiquidType() == CppCauldronLiquidType.WATER)) {
            if (stack.getItem() instanceof DyeItem dyeItem) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), dyeItem));
            } else if (stack.isOf(Items.BONE_MEAL)) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), (DyeItem) Items.WHITE_DYE));
            } else if (stack.isOf(Items.LAPIS_LAZULI)) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), (DyeItem) Items.BLUE_DYE));
            } else if (stack.isOf(Items.COCOA_BEANS)) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), (DyeItem) Items.BROWN_DYE));
            } else if (stack.isOf(Items.INK_SAC)) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), (DyeItem) Items.BLACK_DYE));
            } else return ActionResult.PASS;
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            if (!player.isCreative()) stack.decrement(1);
            cauldron.setLiquidType(CppCauldronLiquidType.COLORED_WATER);
            update(world, pos, state, cauldron, player);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    };
    //炼药锅交互逻辑
    Map<ItemConvertible, CppCauldronBehavior> BEHAVIORS = Util.make(new HashMap<>(), map -> {
        //岩浆，细雪，空桶
        map.put(Items.LAVA_BUCKET, (world, pos, state, cauldron, player, hand, stack) ->
                fill(world, pos, state, cauldron, player, hand, stack, CppCauldronLiquidType.LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, true));
        map.put(Items.POWDER_SNOW_BUCKET, (world, pos, state, cauldron, player, hand, stack) ->
                fill(world, pos, state, cauldron, player, hand, stack, CppCauldronLiquidType.POWDER_SNOW, SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW, false));
        map.put(Items.BUCKET, (world, pos, state, cauldron, player, hand, stack) -> {
            if (cauldron.canDecrease(BUCKET_LEVEL) && !cauldron.isPotion()) {
                if (cauldron.getLiquidType().getBucketItem() != null) {
                    world.playSound(null, pos, cauldron.getLiquidType().getBucketFillSound(), SoundCategory.BLOCKS);
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, cauldron.getLiquidType().getBucketItem().getDefaultStack()));
                    cauldron.decreaseAmount(BUCKET_LEVEL);
                    player.incrementStat(Stats.USED.getOrCreateStat(Items.BUCKET));
                    world.setBlockState(pos, state.with(CppCauldronBlock.EMITS_LIGHT, false));
                    update(world, pos, state, cauldron, player);
                    return ActionResult.SUCCESS;
                } else return ActionResult.PASS;
            } else return ActionResult.PASS;
        });
        //水，药水
        map.put(Items.WATER_BUCKET, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.POTION, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(CauldronppItems.WATER_BOTTLE, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.GLASS_BOTTLE, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.ARROW, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.NETHER_WART, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        for (Item item : PotionHelper.brewingMaterial.keySet()) {
            map.put(item, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        }
        for (Item item : PotionHelper.brewingMaterialType.keySet()) {
            map.put(item, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        }
        //清洗
        map.put(Items.WHITE_BANNER, CLEAN_BANNER);
        map.put(Items.ORANGE_BANNER, CLEAN_BANNER);
        map.put(Items.MAGENTA_BANNER, CLEAN_BANNER);
        map.put(Items.LIGHT_BLUE_BANNER, CLEAN_BANNER);
        map.put(Items.YELLOW_BANNER, CLEAN_BANNER);
        map.put(Items.LIME_BANNER, CLEAN_BANNER);
        map.put(Items.PINK_BANNER, CLEAN_BANNER);
        map.put(Items.GRAY_BANNER, CLEAN_BANNER);
        map.put(Items.LIGHT_GRAY_BANNER, CLEAN_BANNER);
        map.put(Items.CYAN_BANNER, CLEAN_BANNER);
        map.put(Items.PURPLE_BANNER, CLEAN_BANNER);
        map.put(Items.BLUE_BANNER, CLEAN_BANNER);
        map.put(Items.BROWN_BANNER, CLEAN_BANNER);
        map.put(Items.GREEN_BANNER, CLEAN_BANNER);
        map.put(Items.RED_BANNER, CLEAN_BANNER);
        map.put(Items.BLACK_BANNER, CLEAN_BANNER);

        map.put(Items.SHIELD, (world, pos, state, cauldron, player, hand, stack) -> {
            if (cauldron.getLiquidType() == CppCauldronLiquidType.WATER && cauldron.canDecrease(1)) {
                DyeColor color = stack.getOrDefault(DataComponentTypes.BASE_COLOR, null);
                if (color == null) {
                    return ActionResult.PASS;
                } else {
                    if (!world.isClient) {
                        stack.remove(DataComponentTypes.BANNER_PATTERNS);
                        stack.remove(DataComponentTypes.BASE_COLOR);
                        player.incrementStat(Stats.CLEAN_BANNER);
                        cauldron.decreaseAmount(1);
                        update(world, pos, state, cauldron, player);
                    }
                }
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        });

        map.put(Items.WHITE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.ORANGE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.MAGENTA_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIGHT_BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.YELLOW_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIME_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.PINK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIGHT_GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.CYAN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.PURPLE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BROWN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.GREEN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.RED_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BLACK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        //皮质物品清洗，染色
        map.put(Items.LEATHER_HELMET, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_CHESTPLATE, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_LEGGINGS, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_BOOTS, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_HORSE_ARMOR, DYEABLE_ITEM_BEHAVIOR);
        //水染色
        map.put(Items.WHITE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.ORANGE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.MAGENTA_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIGHT_BLUE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.YELLOW_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIME_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.PINK_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.GRAY_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIGHT_GRAY_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.CYAN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.PURPLE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BLUE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BROWN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.GREEN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.RED_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BLACK_DYE, DYE_ITEM_BEHAVIOR);

        map.put(Items.BONE_MEAL, DYE_ITEM_BEHAVIOR);
        map.put(Items.LAPIS_LAZULI, DYE_ITEM_BEHAVIOR);
        map.put(Items.COCOA_BEANS, DYE_ITEM_BEHAVIOR);
        map.put(Items.INK_SAC, DYE_ITEM_BEHAVIOR);
    });
    //填满
    static ActionResult fill(World world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, PlayerEntity player, Hand hand, ItemStack stack, CppCauldronLiquidType liquidType, SoundEvent sound, boolean lit) {
        if (blockEntity.isEmpty()) {
            blockEntity.setLiquidType(liquidType);
            blockEntity.increaseAmount(BUCKET_LEVEL);
            world.playSound(null, pos, sound, SoundCategory.BLOCKS);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.setBlockState(pos, state.with(CppCauldronBlock.EMITS_LIGHT, lit));
            update(world, pos, state, blockEntity, player);
            return ActionResult.SUCCESS;
        } else return ActionResult.PASS;
    }
    //更新
    static void update(World world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, PlayerEntity player) {
        blockEntity.markDirty();
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        world.updateComparators(pos, state.getBlock());
    }
    //水染色
    static int getDyedColor(int oriColor, DyeItem dyeItem) {
        int dyeColor = dyeItem.getColor().getEntityColor();
        return getDyedColor(oriColor, dyeColor);
    }
    static int getDyedColor(int oriColor, int dyeColor) {
        int max = 0;
        int[] results = new int[3];
        oriColor = oriColor & 0xffffff;
        float oriR = (oriColor >> 16 & 0xff) / 255.0f;
        float oriG = (oriColor >> 8 & 0xff) / 255.0f;
        float oriB = (oriColor & 0xff) / 255.0f;
        results[0] += (int) (oriR * 255.0f);
        results[1] += (int) (oriG * 255.0f);
        results[2] += (int) (oriB * 255.0f);
        max += (int) (Math.max(oriR, Math.max(oriG, oriB)) * 255.0f);

        dyeColor = dyeColor & 0xffffff;
        float[] dyeColors = new float[3];
        dyeColors[0] = (dyeColor >> 16 & 0xff) / 255.0f;
        dyeColors[1] = (dyeColor >> 8 & 0xff) / 255.0f;
        dyeColors[2] = (dyeColor & 0xff) / 255.0f;
        int dyeR = (int) (dyeColors[0] * 255.0f);
        int dyeG = (int) (dyeColors[1] * 255.0f);
        int dyeB = (int) (dyeColors[2] * 255.0f);
        max += Math.max(dyeR, Math.max(dyeG, dyeB));
        results[0] += dyeR;
        results[1] += dyeG;
        results[2] += dyeB;

        int resultR = results[0] / 2;
        int resultG = results[1] / 2;
        int resultB = results[2] / 2;

        float avg = (float) max / 2;
        float max2 = Math.max(resultR, Math.max(resultG, resultB));

        resultR = (int) (resultR * avg / max2);
        resultG = (int) (resultG * avg / max2);
        resultB = (int) (resultB * avg / max2);

        return (resultR << 16) | (resultG << 8) | resultB;
    }
}
