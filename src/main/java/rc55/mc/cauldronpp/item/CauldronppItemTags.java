package rc55.mc.cauldronpp.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppItemTags {

    public static final TagKey<Item> BREWING_MATERIALS = register("brewing_materials");
    public static final TagKey<Item> SPLASH_POTIONS = registerC("splash_potions");
    public static final TagKey<Item> LINGERING_POTIONS = registerC("lingering_potions");
    public static final TagKey<Item> TIPPED_ARROWS = registerC("tipped_arrows");

    private static TagKey<Item> register(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Cauldronpp.MODID, id));
    }
    private static TagKey<Item> registerC(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("c", id));
    }
}
