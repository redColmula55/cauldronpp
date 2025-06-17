package rc55.mc.cauldronpp.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class CauldronppItems {

    public static final Item CPP_POTION = register("potion", new CppPotionItem());
    public static final Item CPP_SPLASH_POTION = register("splash_potion", new CppSplashPotionItem());
    public static final Item CPP_LINGERING_POTION = register("lingering_potion", new CppLingeringPotionItem());
    public static final Item CPP_TIPPED_ARROW = register("tipped_arrow", new CppTippedArrowItem());
    public static final Item WATER_BOTTLE = register("water_bottle", new WaterBottleItem());

    private static Item register(String id) {
        return register(id, new Item(new Item.Settings()));
    }
    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Cauldronpp.MODID, id), item);
    }
    public static void regItem() {
        addItemToGroup();
        regItemDispenserBehavior();
        Cauldronpp.LOGGER.info("Cauldron++ items registered.");
    }
    private static void addItemToGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(WATER_BOTTLE);
            entries.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 32767));
            entries.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 16123));
            entries.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 81621));
            entries.add(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 55577));
            entries.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 32767));
            entries.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 16123));
            entries.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 81621));
            entries.add(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 55577));
            entries.add(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 32767));
            entries.add(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 16123));
            entries.add(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 81621));
            entries.add(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 55577));
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(CauldronppBlocks.CPP_CAULDRON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(CauldronppBlocks.CPP_CAULDRON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 32767));
            entries.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 16123));
            entries.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 81621));
            entries.add(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 55577));
        });
    }
    private static void regItemDispenserBehavior() {
        DispenserBlock.registerBehavior(WATER_BOTTLE, new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld serverWorld = pointer.world();
                BlockPos blockPos = pointer.pos();
                BlockPos blockPos2 = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                if (!serverWorld.getBlockState(blockPos2).isIn(BlockTags.CONVERTABLE_TO_MUD)) {
                    return this.fallbackBehavior.dispense(pointer, stack);
                } else {
                    if (!serverWorld.isClient) {
                        for (int i = 0; i < 5; i++) {
                            serverWorld.spawnParticles(
                                    ParticleTypes.SPLASH,
                                    blockPos.getX() + serverWorld.random.nextDouble(),
                                    blockPos.getY() + 1,
                                    blockPos.getZ() + serverWorld.random.nextDouble(),
                                    1,
                                    0.0,
                                    0.0,
                                    0.0,
                                    1.0
                            );
                        }
                    }

                    serverWorld.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    serverWorld.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                    serverWorld.setBlockState(blockPos2, Blocks.MUD.getDefaultState());
                    return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        });
    }
}
