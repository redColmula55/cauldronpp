package rc55.mc.cauldronpp.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rc55.mc.cauldronpp.Cauldronpp;

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
        return Registry.register(Registry.ITEM, new Identifier(Cauldronpp.MODID, id), item);
    }
    public static void regItem() {
        Cauldronpp.LOGGER.info("Cauldron++ items registered.");
    }
}
