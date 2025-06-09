package rc55.mc.cauldronpp.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class LangEnDataGen extends FabricLanguageProvider {
    public LangEnDataGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    private static final String[] potionPrefixesTranslation = new String[]{"Mundane", "Uninteresting", "Bland", "Clear",
            "Milky", "Diffuse", "Artless", "Thin", "Awkward", "Flat",
            "Bulky", "Bungling", "Buttered", "Smooth", "Suave", "Debonair",
            "Thick", "Elegant", "Fancy", "Charming", "Dashing", "Refined",
            "Cordial", "Sparkling", "Potent", "Foul", "Odorless", "Rank",
            "Harsh", "Acrid", "Gross", "Stinky"};

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(CauldronppBlocks.CPP_CAULDRON, "Cauldron(Legacy)");
        builder.add(CauldronppItems.WATER_BOTTLE, "Water Bottle");
        PotionHelper.generatePotionNameTranslation(builder, potionPrefixesTranslation, "Potion", "Splash Potion", "Lingering Potion", "Tipped Arrow");
        builder.add(CauldronppItems.CPP_POTION, "%s Potion");
        builder.add(CauldronppItems.CPP_SPLASH_POTION, "%s Splash Potion");
        builder.add(CauldronppItems.CPP_LINGERING_POTION, "%s Lingering Potion");
        builder.add(CauldronppItems.CPP_TIPPED_ARROW, "%s Arrow");
    }
}
