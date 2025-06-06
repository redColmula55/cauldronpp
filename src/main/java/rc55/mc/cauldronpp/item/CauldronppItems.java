package rc55.mc.cauldronpp.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
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
        return Registry.register(Registries.ITEM, new Identifier(Cauldronpp.MODID, id), item);
    }
    public static void regItem() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(WATER_BOTTLE);
            entries.add(PotionHelper.getPotionItem(0, 32767));
            entries.add(PotionHelper.getPotionItem(0, 16123));
            entries.add(PotionHelper.getPotionItem(0, 81621));
            entries.add(PotionHelper.getPotionItem(0, 55577));
            entries.add(PotionHelper.getPotionItem(1, 32767));
            entries.add(PotionHelper.getPotionItem(1, 16123));
            entries.add(PotionHelper.getPotionItem(1, 81621));
            entries.add(PotionHelper.getPotionItem(1, 55577));
            entries.add(PotionHelper.getPotionItem(2, 32767));
            entries.add(PotionHelper.getPotionItem(2, 16123));
            entries.add(PotionHelper.getPotionItem(2, 81621));
            entries.add(PotionHelper.getPotionItem(2, 55577));
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(CauldronppBlocks.CPP_CAULDRON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(CauldronppBlocks.CPP_CAULDRON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(PotionHelper.getPotionItem(3, 32767));
            entries.add(PotionHelper.getPotionItem(3, 16123));
            entries.add(PotionHelper.getPotionItem(3, 81621));
            entries.add(PotionHelper.getPotionItem(3, 55577));
        });
        Cauldronpp.LOGGER.info("Cauldron++ items registered.");
    }
}
