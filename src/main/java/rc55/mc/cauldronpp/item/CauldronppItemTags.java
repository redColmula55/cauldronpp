package rc55.mc.cauldronpp.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppItemTags {

    public static final TagKey<Item> CAULDRON_BREWING_MATERIALS = register("cauldron_brewing_materials");
    public static final TagKey<Item> POTION_MATERIALS = register("cauldron_brewing_materials/potion");
    public static final TagKey<Item> POTION_TYPE_MATERIALS = register("cauldron_brewing_materials/potion_type");
    public static final TagKey<Item> SPLASH_POTIONS = registerC("potions/splash");
    public static final TagKey<Item> LINGERING_POTIONS = registerC("potions/lingering");
    public static final TagKey<Item> TIPPED_ARROWS = registerC("tipped_arrows");

    private static TagKey<Item> register(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Cauldronpp.MODID, id));
    }
    private static TagKey<Item> registerC(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("c", id));
    }
}
